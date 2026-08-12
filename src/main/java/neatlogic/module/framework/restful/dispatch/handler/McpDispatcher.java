package neatlogic.module.framework.restful.dispatch.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.asynchronization.threadlocal.RequestContext;
import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.asynchronization.threadlocal.UserContext;
import neatlogic.framework.common.util.TenantUtil;
import neatlogic.framework.dto.AuthenticationInfoVo;
import neatlogic.framework.dto.UserVo;
import neatlogic.framework.dto.module.ModuleVo;
import neatlogic.framework.exception.core.ApiRuntimeException;
import neatlogic.framework.exception.type.ApiNotFoundException;
import neatlogic.framework.exception.type.ComponentNotFoundException;
import neatlogic.framework.exception.type.ParamNotExistsException;
import neatlogic.framework.exception.type.PermissionDeniedException;
import neatlogic.framework.restful.core.IApiComponent;
import neatlogic.framework.restful.core.privateapi.PrivateApiComponentFactory;
import neatlogic.framework.restful.dao.mapper.ApiMapper;
import neatlogic.framework.restful.dto.ApiVo;
import neatlogic.framework.restful.enums.ApiType;
import neatlogic.framework.restful.mcp.McpToolMetadataBuilder;
import neatlogic.framework.service.AuthenticationInfoService;
import neatlogic.framework.util.TimeUtil;
import neatlogic.module.framework.filter.handler.BearerTokenAuthHandler;
import neatlogic.module.framework.filter.handler.DefaultLoginAuthHandler;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/mcp/api")
public class McpDispatcher {
    private static final String DEFAULT_PROTOCOL_VERSION = "2025-11-25";
    private static final Set<String> SUPPORT_PROTOCOL_VERSION_SET = new HashSet<>(Arrays.asList("2025-06-18", "2025-11-25"));

    @Resource
    private ApiMapper apiMapper;

    @Resource
    private BearerTokenAuthHandler bearerTokenAuthHandler;

    @Resource
    private DefaultLoginAuthHandler defaultLoginAuthHandler;

    @Resource
    private AuthenticationInfoService authenticationInfoService;

    @PostMapping({"/{tenant}", "/{tenant}/{scope}"})
    public void dispatch(@PathVariable("tenant") String tenant, @PathVariable(value = "scope", required = false) String scope, @RequestBody String body, HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            if (!initContext(tenant, request, response)) {
                return;
            }
            Object payload = parseRequestBody(body);
            if (payload instanceof JSONObject) {
                JSONObject resp = handleSingleRequest(normalizeScope(scope), (JSONObject) payload, request, response);
                if (resp != null) {
                    writeJson(response, resp);
                }
                return;
            }
            if (payload instanceof JSONArray) {
                handleBatchRequest(normalizeScope(scope), (JSONArray) payload, request, response);
                return;
            }
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            writeJson(response, getErrorResponse(null, -32600, "invalid request"));
        } catch (McpProtocolException ex) {
            response.setStatus(ex.getHttpStatus());
            if (ex.isNoBody()) {
                return;
            }
            writeJson(response, getErrorResponse(ex.getId(), ex.getRpcCode(), ex.getMessage()));
        } catch (PermissionDeniedException ex) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            writeJson(response, getErrorResponse(null, -32000, ex.getMessage()));
        } catch (ApiRuntimeException ex) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            writeJson(response, getErrorResponse(null, -32000, ex.getMessage()));
        } catch (Throwable ex) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            writeJson(response, getErrorResponse(null, -32000, ExceptionUtils.getStackTrace(ex)));
        }
    }

    @GetMapping({"/{tenant}", "/{tenant}/{scope}"})
    public void get(@PathVariable("tenant") String tenant, @PathVariable(value = "scope", required = false) String scope, HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
    }

    @DeleteMapping({"/{tenant}", "/{tenant}/{scope}"})
    public void delete(@PathVariable("tenant") String tenant, @PathVariable(value = "scope", required = false) String scope, HttpServletRequest request, HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
    }

    private Object parseRequestBody(String body) {
        if (StringUtils.isBlank(body)) {
            throw new McpProtocolException(HttpServletResponse.SC_BAD_REQUEST, -32600, null, "invalid request");
        }
        try {
            return JSON.parse(body);
        } catch (Exception ex) {
            throw new McpProtocolException(HttpServletResponse.SC_BAD_REQUEST, -32700, null, "parse error");
        }
    }

    private JSONObject handleSingleRequest(String scope, JSONObject req, HttpServletRequest request, HttpServletResponse response) {
        if (isNotificationOrResponse(req) && !isRequest(req)) {
            response.setStatus(HttpServletResponse.SC_ACCEPTED);
            return null;
        }
        return processRequest(scope, req, request, response);
    }

    private void handleBatchRequest(String scope, JSONArray requestArray, HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (requestArray.isEmpty()) {
            throw new McpProtocolException(HttpServletResponse.SC_BAD_REQUEST, -32600, null, "invalid request");
        }
        JSONArray responseArray = new JSONArray();
        boolean hasRequest = false;
        for (int i = 0; i < requestArray.size(); i++) {
            Object item = requestArray.get(i);
            if (!(item instanceof JSONObject)) {
                responseArray.add(getErrorResponse(null, -32600, "invalid request"));
                hasRequest = true;
                continue;
            }
            JSONObject req = (JSONObject) item;
            if (!isRequest(req)) {
                continue;
            }
            hasRequest = true;
            JSONObject resp = processRequest(scope, req, request, response);
            if (resp != null) {
                responseArray.add(resp);
            }
        }
        if (!hasRequest) {
            response.setStatus(HttpServletResponse.SC_ACCEPTED);
            return;
        }
        writeJson(response, responseArray);
    }

    private JSONObject processRequest(String scope, JSONObject req, HttpServletRequest request, HttpServletResponse response) {
        Object id = req.get("id");
        String method = req.getString("method");
        if (StringUtils.isBlank(method)) {
            return getErrorResponse(id, -32600, "invalid request");
        }
        try {
            JSONObject result = new JSONObject();
            switch (method) {
                case "initialize":
                    result = initialize(req.getJSONObject("params"));
                    break;
                case "notifications/initialized":
                    return null;
                case "tools/list":
                    result.put("tools", listTools(scope));
                    break;
                case "tools/call":
                    result = callTool(scope, req.getJSONObject("params"));
                    break;
                default:
                    return getErrorResponse(id, -32601, "method not found: " + method);
            }
            JSONObject resp = new JSONObject();
            resp.put("jsonrpc", "2.0");
            resp.put("result", result);
            resp.put("id", id);
            return resp;
        } catch (McpProtocolException ex) {
            if (ex.getHttpStatus() > 0) {
                response.setStatus(ex.getHttpStatus());
            }
            return getErrorResponse(ex.getId() != null ? ex.getId() : id, ex.getRpcCode(), ex.getMessage());
        } catch (ApiRuntimeException ex) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return getErrorResponse(id, -32000, ex.getMessage());
        } catch (Throwable ex) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return getErrorResponse(id, -32000, ExceptionUtils.getStackTrace(ex));
        }
    }

    private boolean isRequest(JSONObject req) {
        return req != null && req.containsKey("method");
    }

    private boolean isNotificationOrResponse(JSONObject req) {
        return req != null && (!req.containsKey("id") || req.containsKey("result") || req.containsKey("error"));
    }

    private JSONObject initialize(JSONObject params) {
        JSONObject result = new JSONObject();
        String protocolVersion = params == null ? null : params.getString("protocolVersion");
        result.put("protocolVersion", SUPPORT_PROTOCOL_VERSION_SET.contains(protocolVersion) ? protocolVersion : DEFAULT_PROTOCOL_VERSION);
        JSONObject capabilities = new JSONObject();
        capabilities.put("tools", new JSONObject());
        result.put("capabilities", capabilities);
        JSONObject serverInfo = new JSONObject();
        serverInfo.put("name", "neatlogic");
        serverInfo.put("version", "1.0.0");
        result.put("serverInfo", serverInfo);
        result.put("instructions", "This is a stateless MCP HTTP endpoint. Use tools/list to discover tools and tools/call to invoke them. Scope path segments filter tools by module group. Tool metadata may include com.neatlogic extensions in _meta.");
        return result;
    }

    /**
     * 根据地址中的租户和请求认证信息初始化 MCP 调用上下文。
     * 外部客户端使用标准 Bearer PAT，内部 Autoexec 回环保留 BUILDIN 签名认证。
     */
    private boolean initContext(String tenant, HttpServletRequest request, HttpServletResponse response) throws Exception {
        RequestContext.init(request, request.getRequestURI(), response);
        if (StringUtils.isBlank(tenant) || !TenantUtil.hasTenant(tenant)) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return false;
        }
        TenantContext.init().switchTenant(tenant);

        String authorization = request.getHeader("Authorization");
        UserVo userVo = null;
        if (authorization != null && authorization.startsWith("Bearer ")) {
            userVo = bearerTokenAuthHandler.myAuth(request);
        } else if (authorization != null && authorization.startsWith("Bearer_")) {
            // BUILDIN 会同时携带签名中的租户和 Tenant 头，URL 租户必须与它们一致。
            if (Objects.equals(tenant, request.getHeader("Tenant"))) {
                userVo = defaultLoginAuthHandler.myAuth(request);
            }
        }
        if (userVo == null || StringUtils.isBlank(userVo.getUuid()) || StringUtils.isBlank(userVo.getUserId())) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setHeader("WWW-Authenticate", "Bearer realm=\"neatlogic-mcp\"");
            return false;
        }

        userVo.setAuthorization(authorization);
        AuthenticationInfoVo authenticationInfoVo = authenticationInfoService.getAuthenticationInfo(userVo.getUuid());
        UserContext.init(userVo, authenticationInfoVo, TimeUtil.ZONE_TIME);
        request.setAttribute("userId", userVo.getUserId());
        request.setAttribute("userName", userVo.getUserName());
        return true;
    }

    private JSONArray listTools(String scope) throws CloneNotSupportedException {
        JSONArray toolList = new JSONArray();
        List<ApiVo> apiList = getMcpApiList(scope);
        apiList.sort(Comparator.comparing(ApiVo::getModuleGroup, Comparator.nullsFirst(String::compareTo))
                .thenComparing(ApiVo::getToken, Comparator.nullsFirst(String::compareTo)));
        for (ApiVo api : apiList) {
            toolList.add(McpToolMetadataBuilder.buildTool(api));
        }
        return toolList;
    }

    private JSONObject callTool(String scope, JSONObject params) {
        JSONObject result = new JSONObject();
        JSONArray contentList = new JSONArray();
        JSONObject content = new JSONObject();
        content.put("type", "text");
        try {
            if (params == null || StringUtils.isBlank(params.getString("name"))) {
                throw new ParamNotExistsException("name");
            }
            JSONObject arguments = params.getJSONObject("arguments");
            Object output = invokeTool(scope, params.getString("name"), arguments == null ? new JSONObject() : arguments);
            Object jsonOutput = toJsonCompatibleObject(output);
            content.put("text", JSON.toJSONString(jsonOutput));
            result.put("structuredContent", getStructuredContent(jsonOutput));
            result.put("isError", false);
        } catch (Exception ex) {
            content.put("text", ex.getMessage());
            result.put("isError", true);
        }
        contentList.add(content);
        result.put("content", contentList);
        return result;
    }

    private Object invokeTool(String scope, String name, JSONObject arguments) throws Exception {
        for (ApiVo api : getMcpApiList(scope)) {
            if (Objects.equals(name, McpToolMetadataBuilder.getToolName(api)) || Objects.equals(name, api.getToken())) {
                return invokeApi(api.getToken(), arguments);
            }
        }
        throw new ApiNotFoundException(name);
    }

    private Object toJsonCompatibleObject(Object output) {
        if (output == null || output instanceof JSONObject || output instanceof JSONArray || output instanceof String || output instanceof Number || output instanceof Boolean) {
            return output;
        }
        return JSON.toJSON(output);
    }

    private JSONObject getStructuredContent(Object jsonOutput) {
        if (jsonOutput instanceof JSONObject) {
            return (JSONObject) jsonOutput;
        }
        JSONObject structuredContent = new JSONObject();
        structuredContent.put("Return", jsonOutput);
        return structuredContent;
    }


    private List<ApiVo> getMcpApiList(String scope) throws CloneNotSupportedException {
        Map<String, ApiVo> dbMcpApiMap = getDbMcpApiMap();
        List<ApiVo> apiList = new ArrayList<>();
        for (ApiVo api : PrivateApiComponentFactory.getTenantActiveApiList()) {
            ApiVo dbApi = dbMcpApiMap.get(api.getToken());
            if (dbApi == null || !Objects.equals(ApiType.OBJECT.getValue(), api.getType())) {
                continue;
            }
            if (StringUtils.isNotBlank(scope) && !Objects.equals(scope, api.getModuleGroup())) {
                continue;
            }
            ApiVo clonedApi = api.clone();
            clonedApi.setIsMcp(1);
            clonedApi.setQps(dbApi.getQps());
            clonedApi.setNeedAudit(dbApi.getNeedAudit());
            apiList.add(clonedApi);
        }
        return apiList;
    }

    private Map<String, ApiVo> getDbMcpApiMap() {
        List<String> activeModuleIdList = TenantContext.get().getActiveModuleList().stream().map(ModuleVo::getId).collect(Collectors.toList());
        if (activeModuleIdList.isEmpty()) {
            return new HashMap<>();
        }
        return apiMapper.getMcpApiListByModuleId(activeModuleIdList).stream()
                .collect(Collectors.toMap(ApiVo::getToken, api -> api, (a, b) -> a, HashMap::new));
    }

    private Object invokeApi(String token, JSONObject arguments) throws Exception {
        ApiVo dbApiVo = apiMapper.getApiByToken(token);
        if (dbApiVo == null || !Objects.equals(dbApiVo.getIsMcp(), 1) || !Objects.equals(dbApiVo.getIsActive(), 1)) {
            throw new PermissionDeniedException("api is not mcp service: " + token);
        }
        ApiVo apiVo = PrivateApiComponentFactory.getApiByToken(token);
        if (apiVo == null) {
            throw new ApiNotFoundException(token);
        }
        if (!Objects.equals(ApiType.OBJECT.getValue(), apiVo.getType())) {
            throw new PermissionDeniedException("api type is not supported by mcp: " + token);
        }
        IApiComponent comp = PrivateApiComponentFactory.getComponent(apiVo.getHandler(), ApiType.OBJECT, IApiComponent.class);
        if (comp == null) {
            throw new ComponentNotFoundException("接口组件:" + apiVo.getHandler() + "不存在");
        }
        return comp.doService(apiVo, arguments, null);
    }

    private void writeJson(HttpServletResponse response, Object data) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        if (data instanceof JSONObject) {
            response.getWriter().print(((JSONObject) data).toJSONString());
        } else if (data instanceof JSONArray) {
            response.getWriter().print(((JSONArray) data).toJSONString());
        } else {
            response.getWriter().print(JSON.toJSONString(data));
        }
    }

    private JSONObject getErrorResponse(Object id, int code, String message) {
        JSONObject error = new JSONObject();
        error.put("code", code);
        error.put("message", message);
        JSONObject resp = new JSONObject();
        resp.put("jsonrpc", "2.0");
        resp.put("error", error);
        resp.put("id", id);
        return resp;
    }

    private String normalizeScope(String scope) {
        return StringUtils.isBlank(scope) ? null : scope;
    }

    private static class McpProtocolException extends RuntimeException {
        private final int httpStatus;
        private final int rpcCode;
        private final Object id;
        private final boolean noBody;

        private McpProtocolException(int httpStatus, int rpcCode, Object id, String message) {
            this(httpStatus, rpcCode, id, message, false);
        }

        private McpProtocolException(int httpStatus, int rpcCode, Object id, String message, boolean noBody) {
            super(message);
            this.httpStatus = httpStatus;
            this.rpcCode = rpcCode;
            this.id = id;
            this.noBody = noBody;
        }

        public int getHttpStatus() {
            return httpStatus;
        }

        public int getRpcCode() {
            return rpcCode;
        }

        public Object getId() {
            return id;
        }

        public boolean isNoBody() {
            return noBody;
        }
    }
}
