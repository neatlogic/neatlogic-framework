/*Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.*/

package neatlogic.module.framework.restful.dispatch.handler;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONReader;
import neatlogic.framework.asynchronization.threadlocal.InputFromContext;
import neatlogic.framework.asynchronization.threadlocal.RequestContext;
import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.asynchronization.threadlocal.UserContext;
import neatlogic.framework.common.config.Config;
import neatlogic.framework.common.constvalue.InputFrom;
import neatlogic.framework.common.constvalue.ResponseCode;
import neatlogic.framework.common.constvalue.SystemUser;
import neatlogic.framework.common.util.TenantUtil;
import neatlogic.framework.dao.mapper.UserMapper;
import neatlogic.framework.dto.AuthenticationInfoVo;
import neatlogic.framework.dto.UserVo;
import neatlogic.framework.exception.core.ApiRuntimeException;
import neatlogic.framework.exception.tenant.TenantNotFoundException;
import neatlogic.framework.exception.type.ApiNotFoundException;
import neatlogic.framework.exception.type.ComponentNotFoundException;
import neatlogic.framework.exception.type.ParamJSONIrregularException;
import neatlogic.framework.exception.type.PermissionDeniedException;
import neatlogic.framework.restful.auth.core.ApiAuthFactory;
import neatlogic.framework.restful.auth.core.IApiAuth;
import neatlogic.framework.restful.core.IApiComponent;
import neatlogic.framework.restful.core.IBinaryStreamApiComponent;
import neatlogic.framework.restful.core.IJsonStreamApiComponent;
import neatlogic.framework.restful.core.publicapi.PublicApiComponentFactory;
import neatlogic.framework.restful.counter.ApiAccessCountUpdateThread;
import neatlogic.framework.restful.dao.mapper.ApiMapper;
import neatlogic.framework.restful.dto.ApiHandlerVo;
import neatlogic.framework.restful.dto.ApiVo;
import neatlogic.framework.restful.enums.ApiType;
import neatlogic.framework.restful.ratelimiter.RateLimiterTokenBucket;
import neatlogic.framework.service.AuthenticationInfoService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.HandlerMapping;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;

@Deprecated
@Controller
@RequestMapping("/public/api/")
public class PublicApiDispatcher {
    Logger logger = LoggerFactory.getLogger(PublicApiDispatcher.class);

    @Resource
    private ApiMapper apiMapper;


    @Resource
    UserMapper userMapper;

    @Resource
    private AuthenticationInfoService authenticationInfoService;


    private void doIt(HttpServletRequest request, HttpServletResponse response, String token, ApiType apiType, JSONObject paramObj, JSONObject returnObj, String action) throws Exception {
        InputFromContext.init(InputFrom.RESTFUL);
        RequestContext.init(request, token, response);
        //初始化时区
        Cookie[] cookies = request.getCookies();
        String timezone = "+8:00";
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("neatlogic_timezone".equals(cookie.getName())) {
                    timezone = (URLDecoder.decode(cookie.getValue(), "UTF-8"));
                }
            }
        }

        //authorization
        String authorization = request.getHeader("Authorization");

        //初始化租户
        String tenant = request.getHeader("Tenant");
        if (StringUtils.isBlank(tenant) || !TenantUtil.hasTenant(tenant)) {
            throw new TenantNotFoundException(tenant);
        }

        TenantContext.init();
        TenantContext.get().switchTenant(tenant);

        //自定义接口 访问人初始化
        String user = request.getHeader("User");
        UserVo userVo = null;
        if (StringUtils.isNotBlank(user)) {
            UserVo userTmpVo = userMapper.getUserByUuid(user);
            if (userTmpVo != null) {
                userVo = userTmpVo;
                userVo.setAuthorization(authorization);
                AuthenticationInfoVo authenticationInfoVo = authenticationInfoService.getAuthenticationInfo(userVo.getUuid());
                UserContext.init(userVo, authenticationInfoVo, timezone, request, response);
            }
        }
        if (userVo == null) {
            UserContext.init(SystemUser.SYSTEM, request, response);
        }

        UserContext.get().setRequest(request);

        ApiVo interfaceVo = apiMapper.getApiByToken(token);
        String uri = request.getRequestURI();
        /* 如果不是查看帮助接口，则需要校验接口已激活，且此接口对应的handler是public */
        if (interfaceVo == null || (!(uri.contains("/public/api/help/") && !token.contains("/public/api/help/")) && !interfaceVo.getIsActive().equals(1))
                || PublicApiComponentFactory.getApiHandlerByHandler(interfaceVo.getHandler()).isPrivate()) {
            throw new ApiNotFoundException(token);
        }

        // 判断是否master模块接口，如果是不允许访问
        ApiHandlerVo apiHandlerVo = PublicApiComponentFactory.getApiHandlerByHandler(interfaceVo.getHandler());
        if (apiHandlerVo != null) {
            if (apiHandlerVo.getModuleId().equals("master")) {
                throw new PermissionDeniedException();
            }
        } else {
            throw new ComponentNotFoundException("接口组件:" + interfaceVo.getHandler() + "不存在");
        }
        Double qps = interfaceVo.getQps();
        RequestContext.get().setApiRate(qps);
        //从令牌桶拿到令牌才能继续访问，否则直接返回，提示“系统繁忙，请稍后重试”
        if (!RateLimiterTokenBucket.tryAcquire()) {
            response.setStatus(ResponseCode.RATE_LIMITER_TOKEN_BUCKET.getCode());
//            returnObj.put("Message", "系统繁忙，请稍后重试");
            JSONObject returnV = new JSONObject();
            returnV.put("rejectSource", RequestContext.get().getRejectSource().getValue());
            returnV.put("apiRate", RequestContext.get().getApiRate());
            returnV.put("tenantRate", RequestContext.get().getTenantRate());
            returnObj.put("Return", returnV);
            returnObj.put("Status", "ERROR");
            return;
        }
        /*认证，如果是查看帮助接口，则不需要认证*/
        if (!(uri.contains("/public/api/help/") && !token.contains("/public/api/help/"))) {
            IApiAuth apiAuth = ApiAuthFactory.getApiAuth(interfaceVo.getAuthtype());
            if (apiAuth != null) {
                apiAuth.auth(interfaceVo, paramObj, request);
            }
        }

        if (apiType.equals(ApiType.OBJECT)) {
            IApiComponent restComponent = PublicApiComponentFactory.getInstance(interfaceVo.getHandler());
            if (restComponent != null) {
                if (action.equals("doservice")) {
                    /* 统计接口访问次数 **/
                    ApiAccessCountUpdateThread.putToken(token);
                    Long starttime = System.currentTimeMillis();
                    Object returnV = restComponent.doService(interfaceVo, paramObj, response);
                    Long endtime = System.currentTimeMillis();
                    if (!restComponent.isRaw()) {
                        returnObj.put("TimeCost", endtime - starttime);
                        returnObj.put("Return", returnV);
                        returnObj.put("Status", "OK");
                        returnObj.put("sqlList", CollectionUtils.isEmpty(RequestContext.get().getSqlAuditList()) ? null : RequestContext.get().getSqlAuditList());
                    } else {
                        returnObj.putAll(JSONObject.parseObject(JSONObject.toJSONString(returnV)));
                    }
                } else {
                    returnObj.putAll(restComponent.help());
                }
            } else {
                throw new ComponentNotFoundException("接口组件:" + interfaceVo.getHandler() + "不存在");
            }
        } else if (apiType.equals(ApiType.STREAM)) {
            IJsonStreamApiComponent restComponent = PublicApiComponentFactory.getStreamInstance(interfaceVo.getHandler());
            if (restComponent != null) {
                if (action.equals("doservice")) {
                    /* 统计接口访问次数 **/
                    ApiAccessCountUpdateThread.putToken(token);
                    Long starttime = System.currentTimeMillis();
                    Object returnV = restComponent.doService(interfaceVo, paramObj, new JSONReader(new InputStreamReader(request.getInputStream(), StandardCharsets.UTF_8)));
                    Long endtime = System.currentTimeMillis();
                    if (!restComponent.isRaw()) {
                        returnObj.put("TimeCost", endtime - starttime);
                        returnObj.put("Return", returnV);
                        returnObj.put("Status", "OK");
                        returnObj.put("sqlList", CollectionUtils.isEmpty(RequestContext.get().getSqlAuditList()) ? null : RequestContext.get().getSqlAuditList());
                    } else {
                        returnObj.putAll(JSONObject.parseObject(JSONObject.toJSONString(returnV)));
                    }
                } else {
                    returnObj.putAll(restComponent.help());
                }
            } else {
                throw new ComponentNotFoundException("接口组件:" + interfaceVo.getHandler() + "不存在");
            }
        } else if (apiType.equals(ApiType.BINARY)) {
            IBinaryStreamApiComponent restComponent = PublicApiComponentFactory.getBinaryInstance(interfaceVo.getHandler());
            if (restComponent != null) {
                if (action.equals("doservice")) {
                    /* 统计接口访问次数 **/
                    ApiAccessCountUpdateThread.putToken(token);
                    Long starttime = System.currentTimeMillis();
                    Object returnV = restComponent.doService(interfaceVo, paramObj, request, response);
                    Long endtime = System.currentTimeMillis();
                    if (!restComponent.isRaw()) {
                        returnObj.put("TimeCost", endtime - starttime);
                        returnObj.put("Return", returnV);
                        returnObj.put("Status", "OK");
                        returnObj.put("sqlList", CollectionUtils.isEmpty(RequestContext.get().getSqlAuditList()) ? null : RequestContext.get().getSqlAuditList());
                    } else {
                        returnObj.putAll(JSONObject.parseObject(JSONObject.toJSONString(returnV)));
                    }
                } else {
                    returnObj.putAll(restComponent.help());
                }
            } else {
                throw new ComponentNotFoundException("接口组件:" + interfaceVo.getHandler() + "不存在");
            }
        }
    }

    @RequestMapping(value = "/rest/**", method = RequestMethod.GET)
    public void dispatcherForGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String token = new AntPathMatcher().extractPathWithinPattern(pattern, request.getServletPath());
        JSONObject paramObj = new JSONObject();
        Enumeration<String> paraNames = request.getParameterNames();
        while (paraNames.hasMoreElements()) {
            String p = paraNames.nextElement();
            String[] vs = request.getParameterValues(p);
            if (vs.length > 1) {
                paramObj.put(p, vs);
            } else {
                paramObj.put(p, request.getParameter(p));
            }
        }
        JSONObject returnObj = new JSONObject();
        try {
            doIt(request, response, token, ApiType.OBJECT, paramObj, returnObj, "doservice");
        } catch (ApiRuntimeException ex) {
            response.setStatus(ResponseCode.API_RUNTIME.getCode());
            returnObj.put("Status", "ERROR");
            returnObj.put("Message", ex.getMessage());
        } catch (PermissionDeniedException ex) {
            response.setStatus(ResponseCode.PERMISSION_DENIED.getCode());
            returnObj.put("Status", "ERROR");
            returnObj.put("Message", ex.getMessage());
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            response.setStatus(ResponseCode.EXCEPTION.getCode());
            returnObj.put("Status", "ERROR");
            returnObj.put("Message", ExceptionUtils.getStackFrames(ex));
        }
        if (!response.isCommitted()) {
            response.setContentType(Config.RESPONSE_TYPE_JSON);
            response.getWriter().print(returnObj);
        }
    }

    @RequestMapping(value = "/rest/**", method = RequestMethod.POST)
    public void dispatcherForPost(@RequestBody String jsonStr, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String pattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String token = new AntPathMatcher().extractPathWithinPattern(pattern, request.getServletPath());
        
        JSONObject returnObj = new JSONObject();
        try {
            JSONObject paramObj;
            if (StringUtils.isNotBlank(jsonStr)) {
                try {
                    paramObj = JSONObject.parseObject(jsonStr);
                } catch (Exception e) {
                    throw new ParamJSONIrregularException();
                }
            } else {
                paramObj = new JSONObject();
            }

            Enumeration<String> paraNames = request.getParameterNames();
            while (paraNames.hasMoreElements()) {
                String p = paraNames.nextElement();
                String[] vs = request.getParameterValues(p);
                if (vs.length > 1) {
                    paramObj.put(p, vs);
                } else {
                    paramObj.put(p, request.getParameter(p));
                }
            }

            doIt(request, response, token, ApiType.OBJECT, paramObj, returnObj, "doservice");
        } catch (ApiRuntimeException ex) {
            response.setStatus(ResponseCode.API_RUNTIME.getCode());
            returnObj.put("Status", "ERROR");
            returnObj.put("Message", ex.getMessage());
        } catch (PermissionDeniedException ex) {
            response.setStatus(ResponseCode.PERMISSION_DENIED.getCode());
            returnObj.put("Status", "ERROR");
            returnObj.put("Message", ex.getMessage());
        } catch (Exception ex) {
            response.setStatus(ResponseCode.EXCEPTION.getCode());
            returnObj.put("Status", "ERROR");
            returnObj.put("Message", ExceptionUtils.getStackTrace(ex));
            logger.error(ex.getMessage(), ex);
        }
        if (!response.isCommitted()) {
            response.setContentType(Config.RESPONSE_TYPE_JSON);
            response.getWriter().print(returnObj);
        }
    }

    @RequestMapping(value = "/stream/**", method = RequestMethod.POST)
    public void dispatcherForPostStream(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String token = new AntPathMatcher().extractPathWithinPattern(pattern, request.getServletPath());
        JSONObject paramObj = new JSONObject();
        Enumeration<String> paraNames = request.getParameterNames();
        while (paraNames.hasMoreElements()) {
            String p = paraNames.nextElement();
            String[] vs = request.getParameterValues(p);
            if (vs.length > 1) {
                paramObj.put(p, vs);
            } else {
                paramObj.put(p, request.getParameter(p));
            }
        }
        JSONObject returnObj = new JSONObject();
        try {
            doIt(request, response, token, ApiType.STREAM, paramObj, returnObj, "doservice");
        } catch (TenantNotFoundException ex) {
            logger.error(ex.getMessage(), ex);
            response.setStatus(ResponseCode.TENANT_NOTFOUND.getCode());
            returnObj.put("Status", "ERROR");
            returnObj.put("Message", ExceptionUtils.getStackFrames(ex));
        } catch (ApiRuntimeException ex) {
            response.setStatus(ResponseCode.API_RUNTIME.getCode());
            returnObj.put("Status", "ERROR");
            returnObj.put("Message", ex.getMessage());
        } catch (PermissionDeniedException ex) {
            response.setStatus(ResponseCode.PERMISSION_DENIED.getCode());
            returnObj.put("Status", "ERROR");
            returnObj.put("Message", ex.getMessage());
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            response.setStatus(ResponseCode.EXCEPTION.getCode());
            returnObj.put("Status", "ERROR");
            returnObj.put("Message", ExceptionUtils.getStackFrames(ex));
        }
        if (!response.isCommitted()) {
            response.setContentType(Config.RESPONSE_TYPE_JSON);
            response.getWriter().print(returnObj.toJSONString());
        }
    }

    @RequestMapping(value = "/binary/**", method = RequestMethod.GET)
    public void dispatcherForPostBinary(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String token = new AntPathMatcher().extractPathWithinPattern(pattern, request.getServletPath());
        
        JSONObject paramObj = new JSONObject();

        Enumeration<String> paraNames = request.getParameterNames();
        while (paraNames.hasMoreElements()) {
            String p = paraNames.nextElement();
            String[] vs = request.getParameterValues(p);
            if (vs.length > 1) {
                paramObj.put(p, vs);
            } else {
                paramObj.put(p, request.getParameter(p));
            }
        }
        JSONObject returnObj = new JSONObject();
        try {
            doIt(request, response, token, ApiType.BINARY, paramObj, returnObj, "doservice");
        } catch (ApiRuntimeException ex) {
            response.setStatus(ResponseCode.API_RUNTIME.getCode());
            returnObj.put("Status", "ERROR");
            returnObj.put("Message", ex.getMessage());
        } catch (PermissionDeniedException ex) {
            response.setStatus(ResponseCode.PERMISSION_DENIED.getCode());
            returnObj.put("Status", "ERROR");
            returnObj.put("Message", ex.getMessage());
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            response.setStatus(ResponseCode.EXCEPTION.getCode());
            returnObj.put("Status", "ERROR");
            returnObj.put("Message", ExceptionUtils.getStackFrames(ex));
        }
        if (!response.isCommitted()) {
            response.setContentType(Config.RESPONSE_TYPE_JSON);
            response.getWriter().print(returnObj.toJSONString());
        }
    }

    @RequestMapping(value = "/binary/**", method = RequestMethod.POST, consumes = "application/json")
    public void dispatcherForPostBinaryJson(@RequestBody String jsonStr, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String token = new AntPathMatcher().extractPathWithinPattern(pattern, request.getServletPath());
        
        JSONObject paramObj;
        if (StringUtils.isNotBlank(jsonStr)) {
            try {
                paramObj = JSONObject.parseObject(jsonStr);
            } catch (Exception e) {
                throw new ParamJSONIrregularException();
            }
        } else {
            paramObj = new JSONObject();
        }

        JSONObject returnObj = new JSONObject();
        try {
            doIt(request, response, token, ApiType.BINARY, paramObj, returnObj, "doservice");
        } catch (ApiRuntimeException ex) {
            response.setStatus(ResponseCode.API_RUNTIME.getCode());
            returnObj.put("Status", "ERROR");
            returnObj.put("Message", ex.getMessage());
        } catch (PermissionDeniedException ex) {
            response.setStatus(ResponseCode.PERMISSION_DENIED.getCode());
            returnObj.put("Status", "ERROR");
            returnObj.put("Message", ex.getMessage());
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            response.setStatus(ResponseCode.EXCEPTION.getCode());
            returnObj.put("Status", "ERROR");
            returnObj.put("Message", ExceptionUtils.getStackFrames(ex));
        }
        if (!response.isCommitted()) {
            response.setContentType(Config.RESPONSE_TYPE_JSON);
            response.getWriter().print(returnObj.toJSONString());
        }
    }

    @RequestMapping(value = "/binary/**", method = RequestMethod.POST, consumes = "multipart/form-data")
    public void dispatcherForPostBinaryMultipart(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String token = new AntPathMatcher().extractPathWithinPattern(pattern, request.getServletPath());
        
        JSONObject paramObj = new JSONObject();

        Enumeration<String> paraNames = request.getParameterNames();
        while (paraNames.hasMoreElements()) {
            String p = paraNames.nextElement();
            String[] vs = request.getParameterValues(p);
            if (vs.length > 1) {
                paramObj.put(p, vs);
            } else {
                paramObj.put(p, request.getParameter(p));
            }
        }
        JSONObject returnObj = new JSONObject();
        try {
            doIt(request, response, token, ApiType.BINARY, paramObj, returnObj, "doservice");
        } catch (ApiRuntimeException ex) {
            response.setStatus(ResponseCode.API_RUNTIME.getCode());
            returnObj.put("Status", "ERROR");
            returnObj.put("Message", ex.getMessage());
        } catch (PermissionDeniedException ex) {
            response.setStatus(ResponseCode.PERMISSION_DENIED.getCode());
            returnObj.put("Status", "ERROR");
            returnObj.put("Message", ex.getMessage());
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            response.setStatus(ResponseCode.EXCEPTION.getCode());
            returnObj.put("Status", "ERROR");
            returnObj.put("Message", ExceptionUtils.getStackFrames(ex));
        }
        if (!response.isCommitted()) {
            response.setContentType(Config.RESPONSE_TYPE_JSON);
            response.getWriter().print(returnObj.toJSONString());
        }
    }

    @RequestMapping(value = "/help/rest/**", method = RequestMethod.GET)
    public void resthelp(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String token = new AntPathMatcher().extractPathWithinPattern(pattern, request.getServletPath());
        
        JSONObject returnObj = new JSONObject();
        try {
            doIt(request, response, token, ApiType.OBJECT, null, returnObj, "help");
        } catch (ApiRuntimeException ex) {
            response.setStatus(ResponseCode.API_RUNTIME.getCode());
            returnObj.put("Status", "ERROR");
            returnObj.put("Message", ex.getMessage());
        } catch (PermissionDeniedException ex) {
            response.setStatus(ResponseCode.PERMISSION_DENIED.getCode());
            returnObj.put("Status", "ERROR");
            returnObj.put("Message", ex.getMessage());
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            response.setStatus(ResponseCode.EXCEPTION.getCode());
            returnObj.put("Status", "ERROR");
            returnObj.put("Message", ExceptionUtils.getStackFrames(ex));
        }
        response.setContentType(Config.RESPONSE_TYPE_JSON);
        response.getWriter().print(returnObj.toJSONString());
    }

    @RequestMapping(value = "/help/stream/**", method = RequestMethod.GET)
    public void streamhelp(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String token = new AntPathMatcher().extractPathWithinPattern(pattern, request.getServletPath());
        
        JSONObject returnObj = new JSONObject();
        try {
            doIt(request, response, token, ApiType.STREAM, null, returnObj, "help");
        } catch (ApiRuntimeException ex) {
            response.setStatus(ResponseCode.API_RUNTIME.getCode());
            returnObj.put("Status", "ERROR");
            returnObj.put("Message", ex.getMessage());
        } catch (PermissionDeniedException ex) {
            response.setStatus(ResponseCode.PERMISSION_DENIED.getCode());
            returnObj.put("Status", "ERROR");
            returnObj.put("Message", ex.getMessage());
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            response.setStatus(ResponseCode.EXCEPTION.getCode());
            returnObj.put("Status", "ERROR");
            returnObj.put("Message", ExceptionUtils.getStackFrames(ex));
        }
        response.setContentType(Config.RESPONSE_TYPE_JSON);
        response.getWriter().print(returnObj.toJSONString());
    }

    @RequestMapping(value = "/help/binary/**", method = RequestMethod.GET)
    public void binaryhelp(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String token = new AntPathMatcher().extractPathWithinPattern(pattern, request.getServletPath());
        
        JSONObject returnObj = new JSONObject();
        try {
            doIt(request, response, token, ApiType.BINARY, null, returnObj, "help");
        } catch (ApiRuntimeException ex) {
            response.setStatus(ResponseCode.API_RUNTIME.getCode());
            returnObj.put("Status", "ERROR");
            returnObj.put("Message", ex.getMessage());
        } catch (PermissionDeniedException ex) {
            response.setStatus(ResponseCode.PERMISSION_DENIED.getCode());
            returnObj.put("Status", "ERROR");
            returnObj.put("Message", ex.getMessage());
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            response.setStatus(ResponseCode.EXCEPTION.getCode());
            returnObj.put("Status", "ERROR");
            returnObj.put("Message", ExceptionUtils.getStackFrames(ex));
        }
        response.setContentType(Config.RESPONSE_TYPE_JSON);
        response.getWriter().print(returnObj.toJSONString());
    }

}
