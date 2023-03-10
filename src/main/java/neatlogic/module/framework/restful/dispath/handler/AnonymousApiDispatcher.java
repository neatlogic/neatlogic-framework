/*
 * Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package neatlogic.module.framework.restful.dispath.handler;

import neatlogic.framework.asynchronization.threadlocal.RequestContext;
import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.asynchronization.threadlocal.UserContext;
import neatlogic.framework.common.config.Config;
import neatlogic.framework.common.constvalue.SystemUser;
import neatlogic.framework.common.util.RC4Util;
import neatlogic.framework.common.util.TenantUtil;
import neatlogic.framework.dto.FieldValidResultVo;
import neatlogic.framework.exception.core.ApiRuntimeException;
import neatlogic.framework.exception.resubmit.ResubmitException;
import neatlogic.framework.exception.tenant.TenantNotFoundException;
import neatlogic.framework.exception.type.AnonymousExceptionMessage;
import neatlogic.framework.exception.type.ApiNotFoundException;
import neatlogic.framework.exception.type.ComponentNotFoundException;
import neatlogic.framework.exception.type.PermissionDeniedException;
import neatlogic.framework.restful.core.IApiComponent;
import neatlogic.framework.restful.core.IBinaryStreamApiComponent;
import neatlogic.framework.restful.core.IJsonStreamApiComponent;
import neatlogic.framework.restful.core.privateapi.PrivateApiComponentFactory;
import neatlogic.framework.restful.counter.ApiAccessCountUpdateThread;
import neatlogic.framework.restful.dao.mapper.ApiMapper;
import neatlogic.framework.restful.dto.ApiHandlerVo;
import neatlogic.framework.restful.dto.ApiVo;
import neatlogic.framework.restful.enums.ApiType;
import neatlogic.framework.restful.ratelimiter.RateLimiterTokenBucket;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONReader;
import com.alibaba.fastjson.serializer.SerializerFeature;
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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.Objects;

@Controller
@RequestMapping("anonymous/api/")
public class AnonymousApiDispatcher {
    Logger logger = LoggerFactory.getLogger(AnonymousApiDispatcher.class);

    @Resource
    private ApiMapper apiMapper;

    private void doIt(HttpServletRequest request, HttpServletResponse response, String token, boolean tokenHasEncrypted, ApiType apiType, JSONObject paramObj, JSONObject returnObj, String action) throws Exception {
        ApiVo interfaceVo = PrivateApiComponentFactory.getApiByToken(token);

        if (interfaceVo == null) {
            interfaceVo = apiMapper.getApiByToken(token);
            if (interfaceVo == null || !interfaceVo.getIsActive().equals(1)) {
                throw new ApiNotFoundException(token);
            }
        } else if (interfaceVo.getPathVariableObj() != null) {
            // ??????????????????
            paramObj.putAll(interfaceVo.getPathVariableObj());
        }

        // ????????????master???????????????????????????????????????
        ApiHandlerVo apiHandlerVo = PrivateApiComponentFactory.getApiHandlerByHandler(interfaceVo.getHandler());
        if (apiHandlerVo != null) {
            if (apiHandlerVo.getModuleId().equals("master")) {
                throw new PermissionDeniedException();
            }
        } else {
            throw new ComponentNotFoundException("????????????:" + interfaceVo.getHandler() + "?????????");
        }
        Double qps = interfaceVo.getQps();
        ApiVo apiVo = apiMapper.getApiByToken(token);
        if (apiVo != null) {
            qps = apiVo.getQps();
        }
        RequestContext.get().setApiRate(qps);
        //????????????????????????????????????????????????????????????????????????????????????????????????????????????
        if (!RateLimiterTokenBucket.tryAcquire()) {
            response.setStatus(429);
//            returnObj.put("Message", "??????????????????????????????");
            JSONObject returnV = new JSONObject();
            returnV.put("rejectSource", RequestContext.get().getRejectSource().getValue());
            returnV.put("apiRate", RequestContext.get().getApiRate());
            returnV.put("tenantRate", RequestContext.get().getTenantRate());
            returnObj.put("Return", returnV);
            returnObj.put("Status", "ERROR");
            return;
        }
        //??????????????????????????????
        String validField = request.getHeader("neatlogic-validfield");
        if (StringUtils.isNotBlank(validField)) {
            IApiComponent restComponent = PrivateApiComponentFactory.getInstance(interfaceVo.getHandler());
            FieldValidResultVo validResultVo = restComponent.doValid(interfaceVo, paramObj, validField);
            if (StringUtils.isNotBlank(validResultVo.getMsg())) {
                response.setStatus(530);
                returnObj.put("Message", validResultVo.getMsg());
            }
            returnObj.put("Status", validResultVo.getStatus());
        } else {
            if (apiType.equals(ApiType.OBJECT)) {
                IApiComponent restComponent = PrivateApiComponentFactory.getInstance(interfaceVo.getHandler());
                if (restComponent != null) {
                    if (!restComponent.supportAnonymousAccess().isSupportAnonymousAccess()
                            || !Objects.equals(restComponent.supportAnonymousAccess().isRequireTokenEncryption(), tokenHasEncrypted)) {
                        throw new AnonymousExceptionMessage();
                    }
                    if (action.equals("doservice")) {
                        /* ???????????????????????? */
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
                    throw new ComponentNotFoundException("????????????:" + interfaceVo.getHandler() + "?????????");
                }
            } else if (apiType.equals(ApiType.STREAM)) {
                IJsonStreamApiComponent restComponent = PrivateApiComponentFactory.getStreamInstance(interfaceVo.getHandler());
                if (restComponent != null) {
                    if (!restComponent.supportAnonymousAccess().isSupportAnonymousAccess()
                            || !Objects.equals(restComponent.supportAnonymousAccess().isRequireTokenEncryption(), tokenHasEncrypted)) {
                        throw new AnonymousExceptionMessage();
                    }
                    if (action.equals("doservice")) {
                        /* ???????????????????????? */
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
                    throw new ComponentNotFoundException("????????????:" + interfaceVo.getHandler() + "?????????");
                }
            } else if (apiType.equals(ApiType.BINARY)) {
                IBinaryStreamApiComponent restComponent = PrivateApiComponentFactory.getBinaryInstance(interfaceVo.getHandler());
                if (restComponent != null) {
                    if (!restComponent.supportAnonymousAccess().isSupportAnonymousAccess()
                            || !Objects.equals(restComponent.supportAnonymousAccess().isRequireTokenEncryption(), tokenHasEncrypted)) {
                        throw new AnonymousExceptionMessage();
                    }
                    if (action.equals("doservice")) {
                        /* ???????????????????????? */
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
                    throw new ComponentNotFoundException("????????????:" + interfaceVo.getHandler() + "?????????");
                }
            }
        }
    }

    @RequestMapping(value = "/rest/**", method = RequestMethod.GET)
    public void dispatcherForGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String token = new AntPathMatcher().extractPathWithinPattern(pattern, request.getServletPath());
        String tenant;
        boolean tokenHasEncrypted = true;
        JSONObject paramObj = new JSONObject();
        if (token.startsWith(RC4Util.PRE) || token.startsWith(RC4Util.PRE_OLD)) {
            String decryptData = RC4Util.decrypt(token);
            String[] split = decryptData.split("\\?", 2);
            token = split[0].substring(0, split[0].lastIndexOf("/"));
            tenant = split[0].substring(split[0].lastIndexOf("/") + 1);
            if (split.length == 2) {
                String[] params = split[1].split("&");
                for (String param : params) {
                    String[] array = param.split("=", 2);
                    if (array.length == 2) {
                        paramObj.put(array[0], array[1]);
                    }
                }
            }
        } else {
            tokenHasEncrypted = false;
            String originToken = token;
            token = token.substring(0, token.lastIndexOf("/"));
            tenant = originToken.substring(originToken.lastIndexOf("/") + 1);
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
        }
        RequestContext.init(request, token);
        if (TenantUtil.hasTenant(tenant)) {
            TenantContext.init();
            TenantContext.get().switchTenant(tenant);
            UserContext.init(SystemUser.ANONYMOUS.getUserVo(), SystemUser.ANONYMOUS.getTimezone(), request, response);
        }
        JSONObject returnObj = new JSONObject();
        try {
            doIt(request, response, token, tokenHasEncrypted, ApiType.OBJECT, paramObj, returnObj, "doservice");
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

    @RequestMapping(value = "/rest/**", method = RequestMethod.POST)
    public void dispatcherForPost(@RequestBody String jsonStr, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String pattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String token = new AntPathMatcher().extractPathWithinPattern(pattern, request.getServletPath());
        boolean tokenHasEncrypted = false;
        if (token.startsWith(RC4Util.PRE) || token.startsWith(RC4Util.PRE_OLD)) {
            tokenHasEncrypted = true;
            token = RC4Util.decrypt(token);
        }
        /* ?????????gitlab webhook?????????????????????header??????tenant????????????
         ??????header?????????tenant????????????????????????token????????????token??????????????????????????????token??????deploy/ci/gitlab/event/callback/develop???develop??????tenant
        */
        String tenant = request.getHeader("Tenant");
        if (StringUtils.isBlank(tenant)) {
            tenant = token.substring(token.lastIndexOf("/") + 1);
            token = token.substring(0, token.lastIndexOf("/"));
        }
        RequestContext.init(request, token);
        JSONObject returnObj = new JSONObject();
        JSONObject paramObj;
        try {
            if (TenantUtil.hasTenant(tenant)) {
                TenantContext.init();
                TenantContext.get().switchTenant(tenant);
                UserContext.init(SystemUser.ANONYMOUS.getUserVo(), SystemUser.ANONYMOUS.getTimezone(), request, response);
            } else {
                throw new TenantNotFoundException(tenant);
            }
            if (StringUtils.isNotBlank(jsonStr)) {
                try {
                    paramObj = JSONObject.parseObject(jsonStr);
                } catch (Exception e) {
                    throw new ApiRuntimeException("????????????????????????JSON??????");
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

            doIt(request, response, token, tokenHasEncrypted, ApiType.OBJECT, paramObj, returnObj, "doservice");
        } catch (ResubmitException ex) {
            response.setStatus(524);
            if (logger.isWarnEnabled()) {
                logger.warn(ex.getMessage(), ex);
            }
            returnObj.put("Status", "ERROR");
            returnObj.put("Message", ex.getMessage());

        } catch (ApiRuntimeException ex) {
            response.setStatus(520);
            if (logger.isWarnEnabled()) {
                logger.warn(ex.getMessage(), ex);
            }
            returnObj.put("Status", "ERROR");
            returnObj.put("Message", ex.getMessage());
            if (ex.getParam() != null) {
                returnObj.put("Param", ex.getParam());
            }
        } catch (PermissionDeniedException ex) {
            response.setStatus(523);
            if (logger.isWarnEnabled()) {
                logger.warn(ex.getMessage(), ex);
            }
            returnObj.put("Status", "ERROR");
            returnObj.put("Message", ex.getMessage());
        } catch (Exception ex) {
            response.setStatus(500);
            returnObj.put("Status", "ERROR");
            returnObj.put("Message", ExceptionUtils.getStackTrace(ex));
            logger.error(ex.getMessage(), ex);
        }
        if (!response.isCommitted()) {
            response.setContentType(Config.RESPONSE_TYPE_JSON);
            if (returnObj.containsKey("_disableDetect")) {
                returnObj.remove("_disableDetect");
                response.getWriter().print(returnObj.toString(SerializerFeature.DisableCircularReferenceDetect));
            } else {
                response.getWriter().print(returnObj.toJSONString());
            }
        }

    }

    @RequestMapping(value = "/binary/**", method = RequestMethod.GET)
    public void dispatcherForPostBinary(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String token = new AntPathMatcher().extractPathWithinPattern(pattern, request.getServletPath());
        String tenant;
        boolean tokenHasEncrypted = true;
        JSONObject paramObj = new JSONObject();
        if (token.startsWith(RC4Util.PRE) || token.startsWith(RC4Util.PRE_OLD)) {
            String decryptData = RC4Util.decrypt(token);
            String[] split = decryptData.split("\\?", 2);
            token = split[0].substring(0, split[0].lastIndexOf("/"));
            tenant = split[0].substring(split[0].lastIndexOf("/") + 1);
            if (split.length == 2) {
                String[] params = split[1].split("&");
                for (String param : params) {
                    String[] array = param.split("=", 2);
                    if (array.length == 2) {
                        paramObj.put(array[0], array[1]);
                    }
                }
            }
        } else {
            tokenHasEncrypted = false;
            String originToken = token;
            token = token.substring(0, token.lastIndexOf("/"));
            tenant = originToken.substring(originToken.lastIndexOf("/") + 1);
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
        }
        RequestContext.init(request, token);
        if (TenantUtil.hasTenant(tenant)) {
            TenantContext.init();
            TenantContext.get().switchTenant(tenant);
            UserContext.init(SystemUser.ANONYMOUS.getUserVo(), SystemUser.ANONYMOUS.getTimezone(), request, response);
        }
        JSONObject returnObj = new JSONObject();
        try {
            doIt(request, response, token, tokenHasEncrypted, ApiType.BINARY, paramObj, returnObj, "doservice");
        } catch (ResubmitException ex) {
            response.setStatus(524);
            returnObj.put("Status", "ERROR");
            returnObj.put("Message", ex.getMessage());
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
            response.getWriter().print(returnObj.toJSONString());
        }
    }
}
