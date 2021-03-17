package codedriver.framework.restful.web;

import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.asynchronization.threadlocal.UserContext;
import codedriver.framework.common.config.Config;
import codedriver.framework.common.constvalue.SystemUser;
import codedriver.framework.common.util.RC4Util;
import codedriver.framework.common.util.TenantUtil;
import codedriver.framework.dto.FieldValidResultVo;
import codedriver.framework.exception.core.ApiRuntimeException;
import codedriver.framework.exception.resubmit.ResubmitException;
import codedriver.framework.exception.type.AnonymousExceptionMessage;
import codedriver.framework.exception.type.ApiNotFoundException;
import codedriver.framework.exception.type.ComponentNotFoundException;
import codedriver.framework.exception.type.PermissionDeniedException;
import codedriver.framework.restful.core.IApiComponent;
import codedriver.framework.restful.core.IBinaryStreamApiComponent;
import codedriver.framework.restful.core.IJsonStreamApiComponent;
import codedriver.framework.restful.core.privateapi.PrivateApiComponentFactory;
import codedriver.framework.restful.counter.ApiAccessCountUpdateThread;
import codedriver.framework.restful.dao.mapper.ApiMapper;
import codedriver.framework.restful.dto.ApiHandlerVo;
import codedriver.framework.restful.dto.ApiVo;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONReader;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.HandlerMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Controller
@RequestMapping("anonymous/api/")
public class AnonymousApiDispatcher {
    Logger logger = LoggerFactory.getLogger(AnonymousApiDispatcher.class);

    @Resource
    private ApiMapper apiMapper;

    private void doIt(HttpServletRequest request, HttpServletResponse response, String token, ApiVo.Type apiType, JSONObject paramObj, JSONObject returnObj, String action) throws Exception {
        ApiVo interfaceVo = PrivateApiComponentFactory.getApiByToken(token);

        if (interfaceVo == null) {
            interfaceVo = apiMapper.getApiByToken(token);
            if (interfaceVo == null || !interfaceVo.getIsActive().equals(1)) {
                throw new ApiNotFoundException("token为“" + token + "”的接口不存在或已被禁用");
            }
        } else if (interfaceVo.getPathVariableObj() != null) {
            // 融合路径参数
            paramObj.putAll(interfaceVo.getPathVariableObj());
        }

        // 判断是否master模块接口，如果是不允许访问
        ApiHandlerVo apiHandlerVo = PrivateApiComponentFactory.getApiHandlerByHandler(interfaceVo.getHandler());
        if (apiHandlerVo != null) {
            if (apiHandlerVo.getModuleId().equals("master")) {
                throw new PermissionDeniedException();
            }
        } else {
            throw new ComponentNotFoundException("接口组件:" + interfaceVo.getHandler() + "不存在");
        }
        //如果只是接口校验入参
        String validField = request.getHeader("codedriver-validfield");
        if (StringUtils.isNotBlank(validField)) {
            IApiComponent restComponent = PrivateApiComponentFactory.getInstance(interfaceVo.getHandler());
            FieldValidResultVo validResultVo = restComponent.doValid(interfaceVo, paramObj, validField);
            if (StringUtils.isNotBlank(validResultVo.getMsg())) {
                response.setStatus(530);
                returnObj.put("Message", validResultVo.getMsg());
            }
            returnObj.put("Status", validResultVo.getStatus());
        } else {
            if (apiType.equals(ApiVo.Type.OBJECT)) {
                IApiComponent restComponent = PrivateApiComponentFactory.getInstance(interfaceVo.getHandler());
                if (restComponent != null) {
                    if(!restComponent.supportAnonymousAccess()){
                        throw new AnonymousExceptionMessage();
                    }
                    if (action.equals("doservice")) {
                        /* 统计接口访问次数 */
                        ApiAccessCountUpdateThread.putToken(token);
                        Long starttime = System.currentTimeMillis();
                        Object returnV = restComponent.doService(interfaceVo, paramObj, response);
                        Long endtime = System.currentTimeMillis();
                        if (!restComponent.isRaw()) {
                            returnObj.put("TimeCost", endtime - starttime);
                            returnObj.put("Return", returnV);
                            returnObj.put("Status", "OK");
                        } else {
                            returnObj.putAll(JSONObject.parseObject(JSONObject.toJSONString(returnV)));
                        }
                    } else {
                        returnObj.putAll(restComponent.help());
                    }
                } else {
                    throw new ComponentNotFoundException("接口组件:" + interfaceVo.getHandler() + "不存在");
                }
            } else if (apiType.equals(ApiVo.Type.STREAM)) {
                IJsonStreamApiComponent restComponent = PrivateApiComponentFactory.getStreamInstance(interfaceVo.getHandler());
                if (restComponent != null) {
                    if(!restComponent.supportAnonymousAccess()){
                        throw new AnonymousExceptionMessage();
                    }
                    if (action.equals("doservice")) {
                        /* 统计接口访问次数 */
                        ApiAccessCountUpdateThread.putToken(token);
                        Long starttime = System.currentTimeMillis();
                        Object returnV = restComponent.doService(interfaceVo, paramObj, new JSONReader(new InputStreamReader(request.getInputStream(), StandardCharsets.UTF_8)));
                        Long endtime = System.currentTimeMillis();
                        if (!restComponent.isRaw()) {
                            returnObj.put("TimeCost", endtime - starttime);
                            returnObj.put("Return", returnV);
                            returnObj.put("Status", "OK");
                        } else {
                            returnObj.putAll(JSONObject.parseObject(JSONObject.toJSONString(returnV)));
                        }
                    } else {
                        returnObj.putAll(restComponent.help());
                    }
                } else {
                    throw new ComponentNotFoundException("接口组件:" + interfaceVo.getHandler() + "不存在");
                }
            } else if (apiType.equals(ApiVo.Type.BINARY)) {
                IBinaryStreamApiComponent restComponent = PrivateApiComponentFactory.getBinaryInstance(interfaceVo.getHandler());
                if (restComponent != null) {
                    if(!restComponent.supportAnonymousAccess()){
                        throw new AnonymousExceptionMessage();
                    }
                    if (action.equals("doservice")) {
                        /* 统计接口访问次数 */
                        ApiAccessCountUpdateThread.putToken(token);
                        Long starttime = System.currentTimeMillis();
                        Object returnV = restComponent.doService(interfaceVo, paramObj, request, response);
                        Long endtime = System.currentTimeMillis();
                        if (!restComponent.isRaw()) {
                            returnObj.put("TimeCost", endtime - starttime);
                            returnObj.put("Return", returnV);
                            returnObj.put("Status", "OK");
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
    }

    @RequestMapping(value = "/rest/**", method = RequestMethod.GET)
    public void dispatcherForGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String token = new AntPathMatcher().extractPathWithinPattern(pattern, request.getServletPath());
        String decryptData = RC4Util.decrypt(token);
        String[] split = decryptData.split("\\?",2);
        token = split[0].substring(0, split[0].lastIndexOf("/"));
        String tenant = split[0].substring(split[0].lastIndexOf("/") + 1);
        if(TenantUtil.hasTenant(tenant)){
            TenantContext.init();
            TenantContext.get().switchTenant(tenant);
            UserContext.init(SystemUser.ANONYMOUS.getUserVo(), SystemUser.ANONYMOUS.getTimezone(), request, response);
        }
        JSONObject paramObj = new JSONObject();
        if(split.length == 2){
            String[] params = split[1].split("&");
            for(String param : params){
                String[] array = param.split("=",2);
                if(array.length == 2){
                    paramObj.put(array[0], array[1]);
                }
            }
        }
        JSONObject returnObj = new JSONObject();
        try {
            doIt(request, response, token, ApiVo.Type.OBJECT, paramObj, returnObj, "doservice");
        } catch (ApiRuntimeException ex) {
            response.setStatus(520);
            returnObj.put("Status", "ERROR");
            returnObj.put("Message", ex.getMessage());
        } catch (PermissionDeniedException ex) {
            response.setStatus(523);
            returnObj.put("Status", "ERROR");
            returnObj.put("Message", ex.getMessage());
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            response.setStatus(520);
            returnObj.put("Status", "ERROR");
            returnObj.put("Message", ExceptionUtils.getStackFrames(ex));
        }
        if (!response.isCommitted()) {
            response.setContentType(Config.RESPONSE_TYPE_JSON);
            response.getWriter().print(returnObj);
        }
    }

    @RequestMapping(value = "/binary/**", method = RequestMethod.GET)
    public void displatcherForPostBinary(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String token = new AntPathMatcher().extractPathWithinPattern(pattern, request.getServletPath());
        String decryptData = RC4Util.decrypt(token);
        String[] split = decryptData.split("\\?",2);
        token = split[0].substring(0, split[0].lastIndexOf("/"));
        String tenant = split[0].substring(split[0].lastIndexOf("/") + 1);
        if(TenantUtil.hasTenant(tenant)){
            TenantContext.init();
            TenantContext.get().switchTenant(tenant);
            UserContext.init(SystemUser.ANONYMOUS.getUserVo(), SystemUser.ANONYMOUS.getTimezone(), request, response);
        }

        JSONObject paramObj = new JSONObject();
        if(split.length == 2){
            String[] params = split[1].split("&");
            for(String param : params){
                String[] array = param.split("=",2);
                if(array.length == 2){
                    paramObj.put(array[0], array[1]);
                }
            }
        }
        JSONObject returnObj = new JSONObject();
        try {
            doIt(request, response, token, ApiVo.Type.BINARY, paramObj, returnObj, "doservice");
        } catch (ApiRuntimeException ex) {
            response.setStatus(520);
            returnObj.put("Status", "ERROR");
            returnObj.put("Message", ex.getMessage());
        } catch (ResubmitException ex) {
            response.setStatus(524);
            returnObj.put("Status", "ERROR");
            returnObj.put("Message", ex.getMessage());
        } catch (PermissionDeniedException ex) {
            response.setStatus(523);
            returnObj.put("Status", "ERROR");
            returnObj.put("Message", ex.getMessage());
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            response.setStatus(520);
            returnObj.put("Status", "ERROR");
            returnObj.put("Message", ExceptionUtils.getStackFrames(ex));
        }
        if (!response.isCommitted()) {
            response.setContentType(Config.RESPONSE_TYPE_JSON);
            response.getWriter().print(returnObj.toJSONString());
        }
    }
}
