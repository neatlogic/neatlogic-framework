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

package neatlogic.module.framework.restful.dispatch.handler;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONReader;
import com.alibaba.fastjson.serializer.SerializerFeature;
import neatlogic.framework.asynchronization.threadlocal.RequestContext;
import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.asynchronization.threadlocal.UserContext;
import neatlogic.framework.common.config.Config;
import neatlogic.framework.common.constvalue.ResponseCode;
import neatlogic.framework.common.constvalue.SystemUser;
import neatlogic.framework.common.util.RC4Util;
import neatlogic.framework.common.util.TenantUtil;
import neatlogic.framework.dto.FieldValidResultVo;
import neatlogic.framework.exception.core.ApiRuntimeException;
import neatlogic.framework.exception.resubmit.ResubmitException;
import neatlogic.framework.exception.tenant.TenantNotFoundException;
import neatlogic.framework.exception.type.*;
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
import neatlogic.framework.util.AnonymousApiTokenUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.PathVariable;
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
@RequestMapping({"anonymous/api/", "any/api/"})
public class AnonymousApiDispatcher {
    Logger logger = LoggerFactory.getLogger(AnonymousApiDispatcher.class);

    @Resource
    private ApiMapper apiMapper;

    private void doIt(HttpServletRequest request, HttpServletResponse response, String token, boolean tokenHasEncrypted, ApiType apiType, JSONObject paramObj, JSONObject returnObj, String action) throws Exception {
        ApiVo interfaceVo = PrivateApiComponentFactory.getApiByToken(token);
        RequestContext.init(request, token, response);
        if (interfaceVo == null) {
            interfaceVo = apiMapper.getApiByToken(token);
            if (interfaceVo == null || !interfaceVo.getIsActive().equals(1)) {
                throw new ApiNotFoundException(token);
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
            throw new ComponentNotFoundException(interfaceVo.getHandler());
        }
        Double qps = interfaceVo.getQps();
        ApiVo apiVo = apiMapper.getApiByToken(token);
        if (apiVo != null) {
            qps = apiVo.getQps();
        }
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
        //如果只是接口校验入参
        String validField = request.getHeader("neatlogic-validfield");
        if (StringUtils.isNotBlank(validField)) {
            IApiComponent restComponent = PrivateApiComponentFactory.getInstance(interfaceVo.getHandler());
            FieldValidResultVo validResultVo = restComponent.doValid(interfaceVo, paramObj, validField);
            if (StringUtils.isNotBlank(validResultVo.getMsg())) {
                response.setStatus(ResponseCode.API_FIELD_INVALID.getCode());
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
                        /* 统计接口访问次数 */
                        ApiAccessCountUpdateThread.putToken(token);
                        Long startTime = System.currentTimeMillis();
                        Object returnV = restComponent.doService(interfaceVo, paramObj, response);
                        Long endTime = System.currentTimeMillis();
                        if (!restComponent.isRaw()) {
                            returnObj.put("TimeCost", endTime - startTime);
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
                    throw new ComponentNotFoundException(interfaceVo.getHandler());
                }
            } else if (apiType.equals(ApiType.STREAM)) {
                IJsonStreamApiComponent restComponent = PrivateApiComponentFactory.getStreamInstance(interfaceVo.getHandler());
                if (restComponent != null) {
                    if (!restComponent.supportAnonymousAccess().isSupportAnonymousAccess()
                            || !Objects.equals(restComponent.supportAnonymousAccess().isRequireTokenEncryption(), tokenHasEncrypted)) {
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
                            returnObj.put("sqlList", CollectionUtils.isEmpty(RequestContext.get().getSqlAuditList()) ? null : RequestContext.get().getSqlAuditList());
                        } else {
                            returnObj.putAll(JSONObject.parseObject(JSONObject.toJSONString(returnV)));
                        }
                    } else {
                        returnObj.putAll(restComponent.help());
                    }
                } else {
                    throw new ComponentNotFoundException(interfaceVo.getHandler());
                }
            } else if (apiType.equals(ApiType.BINARY)) {
                IBinaryStreamApiComponent restComponent = PrivateApiComponentFactory.getBinaryInstance(interfaceVo.getHandler());
                if (restComponent != null) {
                    if (!restComponent.supportAnonymousAccess().isSupportAnonymousAccess()
                            || !Objects.equals(restComponent.supportAnonymousAccess().isRequireTokenEncryption(), tokenHasEncrypted)) {
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
                            returnObj.put("sqlList", CollectionUtils.isEmpty(RequestContext.get().getSqlAuditList()) ? null : RequestContext.get().getSqlAuditList());
                        } else {
                            returnObj.putAll(JSONObject.parseObject(JSONObject.toJSONString(returnV)));
                        }
                    } else {
                        returnObj.putAll(restComponent.help());
                    }
                } else {
                    throw new ComponentNotFoundException(interfaceVo.getHandler());
                }
            }
        }
    }

    @RequestMapping(value = "/t/{tenant}/rest/**", method = RequestMethod.GET)
    public void dispatcherForGet(@PathVariable("tenant") String tenant, HttpServletRequest request, HttpServletResponse response) throws IOException {
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
        if (TenantUtil.hasTenant(tenant)) {
            TenantContext.init();
            TenantContext.get().switchTenant(tenant);
            UserContext.init(SystemUser.ANONYMOUS, request, response);
        }
        JSONObject returnObj = new JSONObject();
        try {
            doIt(request, response, token, false, ApiType.OBJECT, paramObj, returnObj, "doservice");
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
            response.setStatus(ResponseCode.API_RUNTIME.getCode());
            returnObj.put("Status", "ERROR");
            returnObj.put("Message", ExceptionUtils.getStackFrames(ex));
        }
        if (!response.isCommitted()) {
            response.setContentType(Config.RESPONSE_TYPE_JSON);
            response.getWriter().print(returnObj);
        }
    }

    @RequestMapping(value = "/t/{tenant}/rest/**", method = RequestMethod.POST)
    public void dispatcherForPost(@PathVariable("tenant") String tenant, @RequestBody String jsonStr, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String pattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String token = new AntPathMatcher().extractPathWithinPattern(pattern, request.getServletPath());
        boolean tokenHasEncrypted = false;
        JSONObject returnObj = new JSONObject();
        JSONObject paramObj;
        try {
            if (TenantUtil.hasTenant(tenant)) {
                TenantContext.init();
                TenantContext.get().switchTenant(tenant);
                UserContext.init(SystemUser.ANONYMOUS, request, response);
            } else {
                throw new TenantNotFoundException(tenant);
            }
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

            doIt(request, response, token, tokenHasEncrypted, ApiType.OBJECT, paramObj, returnObj, "doservice");
        } catch (ResubmitException ex) {
            response.setStatus(ResponseCode.RESUBMIT.getCode());
            if (logger.isWarnEnabled()) {
                logger.warn(ex.getMessage(), ex);
            }
            returnObj.put("Status", "ERROR");
            returnObj.put("Message", ex.getMessage());
        } catch (ApiRuntimeException ex) {
            response.setStatus(ResponseCode.API_RUNTIME.getCode());
            if (logger.isWarnEnabled()) {
                logger.warn(ex.getMessage(), ex);
            }
            returnObj.put("Status", "ERROR");
            returnObj.put("Message", ex.getMessage());
            if (ex.getParam() != null) {
                returnObj.put("Param", ex.getParam());
            }
        } catch (PermissionDeniedException ex) {
            response.setStatus(ResponseCode.PERMISSION_DENIED.getCode());
            if (logger.isWarnEnabled()) {
                logger.warn(ex.getMessage(), ex);
            }
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
            if (returnObj.containsKey("_disableDetect")) {
                returnObj.remove("_disableDetect");
                response.getWriter().print(returnObj.toString(SerializerFeature.DisableCircularReferenceDetect));
            } else {
                response.getWriter().print(returnObj.toJSONString());
            }
        }
    }

    @RequestMapping(value = "/t/{tenant}/binary/**", method = RequestMethod.POST, consumes = "multipart/form-data")
    public void dispatcherForPostBinaryMultipart(@PathVariable("tenant") String tenant, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String token = new AntPathMatcher().extractPathWithinPattern(pattern, request.getServletPath());
        JSONObject paramObj = new JSONObject();
        boolean tokenHasEncrypted = false;
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
            if (TenantUtil.hasTenant(tenant)) {
                TenantContext.init();
                TenantContext.get().switchTenant(tenant);
                UserContext.init(SystemUser.ANONYMOUS, request, response);
            } else {
                throw new TenantNotFoundException(tenant);
            }

            doIt(request, response, token, tokenHasEncrypted, ApiType.BINARY, paramObj, returnObj, "doservice");
        } catch (ResubmitException ex) {
            response.setStatus(ResponseCode.RESUBMIT.getCode());
            if (logger.isWarnEnabled()) {
                logger.warn(ex.getMessage(), ex);
            }
            returnObj.put("Status", "ERROR");
            returnObj.put("Message", ex.getMessage());
        } catch (ApiRuntimeException ex) {
            response.setStatus(ResponseCode.API_RUNTIME.getCode());
            if (logger.isWarnEnabled()) {
                logger.warn(ex.getMessage(), ex);
            }
            returnObj.put("Status", "ERROR");
            returnObj.put("Message", ex.getMessage());
            if (ex.getParam() != null) {
                returnObj.put("Param", ex.getParam());
            }
        } catch (PermissionDeniedException ex) {
            response.setStatus(ResponseCode.PERMISSION_DENIED.getCode());
            if (logger.isWarnEnabled()) {
                logger.warn(ex.getMessage(), ex);
            }
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

    @RequestMapping(value = "/t/{tenant}/binary/**", method = RequestMethod.GET)
    public void dispatcherForGetBinary(@PathVariable("tenant") String tenant, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String token = new AntPathMatcher().extractPathWithinPattern(pattern, request.getServletPath());
        JSONObject paramObj = new JSONObject();
        boolean tokenHasEncrypted = false;
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
            if (TenantUtil.hasTenant(tenant)) {
                TenantContext.init();
                TenantContext.get().switchTenant(tenant);
                UserContext.init(SystemUser.ANONYMOUS, request, response);
            } else {
                throw new TenantNotFoundException(tenant);
            }

            doIt(request, response, token, tokenHasEncrypted, ApiType.BINARY, paramObj, returnObj, "doservice");
        } catch (ResubmitException ex) {
            response.setStatus(ResponseCode.RESUBMIT.getCode());
            if (logger.isWarnEnabled()) {
                logger.warn(ex.getMessage(), ex);
            }
            returnObj.put("Status", "ERROR");
            returnObj.put("Message", ex.getMessage());
        } catch (ApiRuntimeException ex) {
            response.setStatus(ResponseCode.API_RUNTIME.getCode());
            if (logger.isWarnEnabled()) {
                logger.warn(ex.getMessage(), ex);
            }
            returnObj.put("Status", "ERROR");
            returnObj.put("Message", ex.getMessage());
            if (ex.getParam() != null) {
                returnObj.put("Param", ex.getParam());
            }
        } catch (PermissionDeniedException ex) {
            response.setStatus(ResponseCode.PERMISSION_DENIED.getCode());
            if (logger.isWarnEnabled()) {
                logger.warn(ex.getMessage(), ex);
            }
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

    @RequestMapping(value = "/rest/**", method = RequestMethod.GET)
    public void dispatcherForGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String token = new AntPathMatcher().extractPathWithinPattern(pattern, request.getServletPath());
        String tenant;
        boolean tokenHasEncrypted = true;
        JSONObject paramObj = new JSONObject();
        if (token.startsWith(RC4Util.PRE) || token.startsWith(RC4Util.PRE_OLD)) {
            JSONObject resultObj = AnonymousApiTokenUtil.decrypt(token);
            token = resultObj.getString("token");
            tenant = resultObj.getString("tenant");
            paramObj.putAll(resultObj.getJSONObject("paramObj"));
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
        if (TenantUtil.hasTenant(tenant)) {
            TenantContext.init();
            TenantContext.get().switchTenant(tenant);
            UserContext.init(SystemUser.ANONYMOUS, request, response);
        }
        JSONObject returnObj = new JSONObject();
        try {
            doIt(request, response, token, tokenHasEncrypted, ApiType.OBJECT, paramObj, returnObj, "doservice");
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
            response.setStatus(ResponseCode.API_RUNTIME.getCode());
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
        /* 为兼容gitlab webhook等场景下无法从header传入tenant的问题，
         先从header里获取tenant，如果没有，则从token中获取，token形如（明文或解密后的token）：deploy/ci/gitlab/event/callback/develop，develop即为tenant
        */
        String tenant = request.getHeader("Tenant");
        if (StringUtils.isBlank(tenant)) {
            tenant = token.substring(token.lastIndexOf("/") + 1);
            token = token.substring(0, token.lastIndexOf("/"));
        }
        JSONObject returnObj = new JSONObject();
        JSONObject paramObj;
        try {
            if (TenantUtil.hasTenant(tenant)) {
                TenantContext.init();
                TenantContext.get().switchTenant(tenant);
                UserContext.init(SystemUser.ANONYMOUS, request, response);
            } else {
                throw new TenantNotFoundException(tenant);
            }
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

            doIt(request, response, token, tokenHasEncrypted, ApiType.OBJECT, paramObj, returnObj, "doservice");
        } catch (ResubmitException ex) {
            response.setStatus(ResponseCode.RESUBMIT.getCode());
            if (logger.isWarnEnabled()) {
                logger.warn(ex.getMessage(), ex);
            }
            returnObj.put("Status", "ERROR");
            returnObj.put("Message", ex.getMessage());

        } catch (ApiRuntimeException ex) {
            response.setStatus(ResponseCode.API_RUNTIME.getCode());
            if (logger.isWarnEnabled()) {
                logger.warn(ex.getMessage(), ex);
            }
            returnObj.put("Status", "ERROR");
            returnObj.put("Message", ex.getMessage());
            if (ex.getParam() != null) {
                returnObj.put("Param", ex.getParam());
            }
        } catch (PermissionDeniedException ex) {
            response.setStatus(ResponseCode.PERMISSION_DENIED.getCode());
            if (logger.isWarnEnabled()) {
                logger.warn(ex.getMessage(), ex);
            }
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
            JSONObject resultObj = AnonymousApiTokenUtil.decrypt(token);
            token = resultObj.getString("token");
            tenant = resultObj.getString("tenant");
            paramObj.putAll(resultObj.getJSONObject("paramObj"));
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
        if (TenantUtil.hasTenant(tenant)) {
            TenantContext.init();
            TenantContext.get().switchTenant(tenant);
            UserContext.init(SystemUser.ANONYMOUS, request, response);
        }
        JSONObject returnObj = new JSONObject();
        try {
            doIt(request, response, token, tokenHasEncrypted, ApiType.BINARY, paramObj, returnObj, "doservice");
        } catch (ResubmitException ex) {
            response.setStatus(ResponseCode.RESUBMIT.getCode());
            returnObj.put("Status", "ERROR");
            returnObj.put("Message", ex.getMessage());
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
            response.setStatus(ResponseCode.API_RUNTIME.getCode());
            returnObj.put("Status", "ERROR");
            returnObj.put("Message", ExceptionUtils.getStackFrames(ex));
        }
        if (!response.isCommitted()) {
            response.setContentType(Config.RESPONSE_TYPE_JSON);
            response.getWriter().print(returnObj.toJSONString());
        }
    }
}
