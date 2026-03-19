/*
 *
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the NeatLogic Sustainable Use License (NSUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */

package neatlogic.module.framework.restful.dispatch.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONReader;
import com.alibaba.fastjson.serializer.SerializerFeature;
import neatlogic.framework.asynchronization.threadlocal.InputFromContext;
import neatlogic.framework.asynchronization.threadlocal.RequestContext;
import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.asynchronization.threadlocal.UserContext;
import neatlogic.framework.common.config.Config;
import neatlogic.framework.common.constvalue.InputFrom;
import neatlogic.framework.common.constvalue.ResponseCode;
import neatlogic.framework.common.constvalue.systemuser.SystemUser;
import neatlogic.framework.common.util.RC4Util;
import neatlogic.framework.common.util.TenantUtil;
import neatlogic.framework.dto.FieldValidResultVo;
import neatlogic.framework.exception.core.ApiRuntimeException;
import neatlogic.framework.exception.core.NotFoundEditTargetException;
import neatlogic.framework.exception.resubmit.ResubmitException;
import neatlogic.framework.exception.tenant.TenantNotFoundException;
import neatlogic.framework.exception.type.*;
import neatlogic.framework.restful.core.IApiComponent;
import neatlogic.framework.restful.core.privateapi.binarystream.IBinaryStreamApiComponent;
import neatlogic.framework.restful.core.privateapi.jsonstream.IJsonStreamApiComponent;
import neatlogic.framework.restful.core.privateapi.raw.IRawApiComponent;
import neatlogic.framework.restful.core.privateapi.PrivateApiComponentFactory;
import neatlogic.framework.restful.dao.mapper.ApiMapper;
import neatlogic.framework.restful.dto.ApiHandlerVo;
import neatlogic.framework.restful.dto.ApiVo;
import neatlogic.framework.restful.enums.ApiType;
import neatlogic.framework.restful.ratelimiter.RateLimiterTokenBucket;
import neatlogic.framework.util.$;
import neatlogic.framework.util.AnonymousApiTokenUtil;
import neatlogic.module.framework.restful.counter.ApiAccessCountService;
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

    @Resource
    private ApiAccessCountService apiAccessCountService;

    private void doIt(HttpServletRequest request, HttpServletResponse response, String token, boolean tokenHasEncrypted, ApiType apiType, JSONObject paramObj, JSONObject returnObj, String action) throws Exception {
        UserContext userContext = UserContext.get();
        if (userContext != null) {
            request.setAttribute("userId", userContext.getUserId());
            request.setAttribute("userName", userContext.getUserName());
        }
        InputFrom inputFrom = null;
        String source = request.getHeader("source");
        if (StringUtils.isNotBlank(source)) {
            inputFrom = InputFrom.get(source);
        }
        if (inputFrom == null) {
            inputFrom = InputFrom.UNKNOWN;
        }
        InputFromContext.init(inputFrom);
        ApiVo interfaceVo = PrivateApiComponentFactory.getApiByToken(token);
        RequestContext.init(request, token, response).setParam(JSON.toJSONString(paramObj, SerializerFeature.PrettyFormat));
        ApiVo dbApiVo = apiMapper.getApiByToken(token);
        if (interfaceVo == null) {
            if (dbApiVo != null) {
                interfaceVo = dbApiVo;
            }
            if (interfaceVo == null || !interfaceVo.getIsActive().equals(1)) {
                throw new ApiNotFoundException(token);
            }
        } else {
            if (interfaceVo.getPathVariableObj() != null) {
                // 融合路径参数
                paramObj.putAll(interfaceVo.getPathVariableObj());
            }
            if (dbApiVo != null) {
                interfaceVo.setQps(dbApiVo.getQps());
                interfaceVo.setNeedAudit(dbApiVo.getNeedAudit());
            }
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
                        apiAccessCountService.putToken(token);
                        Long startTime = System.currentTimeMillis();
                        Object returnV = restComponent.doService(interfaceVo, paramObj, response);
                        Long endTime = System.currentTimeMillis();
                        if (!restComponent.isRaw()) {
                            returnObj.put("TimeCost", endTime - startTime);
                            returnObj.put("Return", returnV);
                            returnObj.put("Status", "OK");
//                            returnObj.put("sqlList", CollectionUtils.isEmpty(RequestContext.get().getSqlAuditList()) ? null : RequestContext.get().getSqlAuditList());
                            returnObj.put("requestSqlAudit", RequestContext.get().getRequestSqlAuditVo());
                        } else {
                            returnObj.putAll(JSON.parseObject(JSON.toJSONString(returnV)));
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
                        apiAccessCountService.putToken(token);
                        Long starttime = System.currentTimeMillis();
                        Object returnV = restComponent.doService(interfaceVo, paramObj, new JSONReader(new InputStreamReader(request.getInputStream(), StandardCharsets.UTF_8)));
                        Long endtime = System.currentTimeMillis();
                        if (!restComponent.isRaw()) {
                            returnObj.put("TimeCost", endtime - starttime);
                            returnObj.put("Return", returnV);
                            returnObj.put("Status", "OK");
//                            returnObj.put("sqlList", CollectionUtils.isEmpty(RequestContext.get().getSqlAuditList()) ? null : RequestContext.get().getSqlAuditList());
                            returnObj.put("requestSqlAudit", RequestContext.get().getRequestSqlAuditVo());
                        } else {
                            returnObj.putAll(JSON.parseObject(JSON.toJSONString(returnV)));
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
                        apiAccessCountService.putToken(token);
                        Long starttime = System.currentTimeMillis();
                        Object returnV = restComponent.doService(interfaceVo, paramObj, request, response);
                        Long endtime = System.currentTimeMillis();
                        if (!restComponent.isRaw()) {
                            returnObj.put("TimeCost", endtime - starttime);
                            returnObj.put("Return", returnV);
                            returnObj.put("Status", "OK");
//                            returnObj.put("sqlList", CollectionUtils.isEmpty(RequestContext.get().getSqlAuditList()) ? null : RequestContext.get().getSqlAuditList());
                            returnObj.put("requestSqlAudit", RequestContext.get().getRequestSqlAuditVo());
                        } else {
                            returnObj.putAll(JSON.parseObject(JSON.toJSONString(returnV)));
                        }
                    } else {
                        returnObj.putAll(restComponent.help());
                    }
                } else {
                    throw new ComponentNotFoundException(interfaceVo.getHandler());
                }
            } else if (apiType.equals(ApiType.RAW)) {
                IRawApiComponent restComponent = PrivateApiComponentFactory.getRawInstance(interfaceVo.getHandler());
                if (restComponent != null) {
                    if (!restComponent.supportAnonymousAccess().isSupportAnonymousAccess()
                            || !Objects.equals(restComponent.supportAnonymousAccess().isRequireTokenEncryption(), tokenHasEncrypted)) {
                        throw new AnonymousExceptionMessage();
                    }
                    if (action.equals("doservice")) {
                        /* 统计接口访问次数 */
                        apiAccessCountService.putToken(token);
                        Long starttime = System.currentTimeMillis();
                        Object returnV = restComponent.doService(interfaceVo, paramObj.getString("payload"), response);
                        Long endtime = System.currentTimeMillis();
                        if (!restComponent.isRaw()) {
                            returnObj.put("TimeCost", endtime - starttime);
                            returnObj.put("Return", returnV);
                            returnObj.put("Status", "OK");
//                            returnObj.put("sqlList", CollectionUtils.isEmpty(RequestContext.get().getSqlAuditList()) ? null : RequestContext.get().getSqlAuditList());
                            returnObj.put("requestSqlAudit", RequestContext.get().getRequestSqlAuditVo());
                            if (restComponent.disableReturnCircularReferenceDetect()) {
                                returnObj.put("_disableDetect", true);
                            }
                        } else {
                            if (restComponent.disableReturnCircularReferenceDetect()) {
                                returnObj.putAll(JSON.parseObject(JSON.toJSONString(returnV, SerializerFeature.DisableCircularReferenceDetect)));
                            } else {
                                returnObj.putAll(JSON.parseObject(JSON.toJSONString(returnV)));
                            }
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
            RequestContext.init(request, request.getRequestURI(), response);
            UserContext.init(SystemUser.ANONYMOUS);
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
                RequestContext.init(request, request.getRequestURI(), response);
            UserContext.init(SystemUser.ANONYMOUS);
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

    @RequestMapping(value = "/t/{tenant}/raw/**", method = RequestMethod.POST)
    public void dispatcherForRawPost(@PathVariable("tenant") String tenant, @RequestBody String jsonStr, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String pattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String token = new AntPathMatcher().extractPathWithinPattern(pattern, request.getServletPath());
        JSONObject returnObj = new JSONObject();
        try {
            if (TenantUtil.hasTenant(tenant)) {
                TenantContext.init();
                TenantContext.get().switchTenant(tenant);
                RequestContext.init(request, request.getRequestURI(), response);
            UserContext.init(SystemUser.ANONYMOUS);
            } else {
                throw new TenantNotFoundException(tenant);
            }
            //由于统一接受json参数，先封装后拆解
            boolean tokenHasEncrypted = false;
            JSONObject jsonObj = new JSONObject();
            if (jsonStr.startsWith("\"") && jsonStr.endsWith("\"")) {
                jsonStr = jsonStr.substring(1, jsonStr.length() - 1);
            }
            jsonObj.put("payload", jsonStr);
            doIt(request, response, token, tokenHasEncrypted, ApiType.RAW, jsonObj, returnObj, "doservice");
        } catch (ResubmitException ex) {
            response.setStatus(ResponseCode.RESUBMIT.getCode());
            if (logger.isWarnEnabled()) {
                logger.warn(ex.getMessage(), ex);
            }
            returnObj.put("Status", "ERROR");
            returnObj.put("Message", ex.getMessage());
        } catch (LicenseInvalidException | LicenseExpiredException ex) {
            response.setStatus(ResponseCode.LICENSE_INVALID.getCode());
            logger.error(ex.getMessage());
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
        } catch (NotFoundEditTargetException ex) {
            response.setStatus(ResponseCode.EDIT_TARGET_NOTFOUND.getCode());
            if (logger.isWarnEnabled()) {
                logger.warn(ex.getMessage(), ex);
            }
            returnObj.put("Status", "ERROR");
            returnObj.put("Message", ex.getMessage());
        } catch (PermissionDeniedException ex) {
            response.setStatus(ResponseCode.PERMISSION_DENIED.getCode());
            if (logger.isWarnEnabled()) {
                logger.warn(ex.getMessage(), ex);
            }
            returnObj.put("Status", "ERROR");
            returnObj.put("Message", $.t(ex.getMessage(), ex.getValues()));
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
                RequestContext.init(request, request.getRequestURI(), response);
            UserContext.init(SystemUser.ANONYMOUS);
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
                RequestContext.init(request, request.getRequestURI(), response);
            UserContext.init(SystemUser.ANONYMOUS);
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
            RequestContext.init(request, request.getRequestURI(), response);
            UserContext.init(SystemUser.ANONYMOUS);
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
                RequestContext.init(request, request.getRequestURI(), response);
            UserContext.init(SystemUser.ANONYMOUS);
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
            RequestContext.init(request, request.getRequestURI(), response);
            UserContext.init(SystemUser.ANONYMOUS);
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
