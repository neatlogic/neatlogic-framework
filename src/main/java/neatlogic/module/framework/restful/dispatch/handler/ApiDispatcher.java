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
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import neatlogic.framework.asynchronization.threadlocal.InputFromContext;
import neatlogic.framework.asynchronization.threadlocal.RequestContext;
import neatlogic.framework.asynchronization.threadlocal.UserContext;
import neatlogic.framework.common.config.Config;
import neatlogic.framework.common.constvalue.InputFrom;
import neatlogic.framework.common.constvalue.ResponseCode;
import neatlogic.framework.dto.FieldValidResultVo;
import neatlogic.framework.exception.core.ApiRuntimeException;
import neatlogic.framework.exception.core.NotFoundEditTargetException;
import neatlogic.framework.exception.resubmit.ResubmitException;
import neatlogic.framework.exception.type.*;
import neatlogic.framework.restful.core.IApiComponent;
import neatlogic.framework.restful.core.privateapi.PrivateApiComponentFactory;
import neatlogic.framework.restful.core.privateapi.binarystream.IBinaryStreamApiComponent;
import neatlogic.framework.restful.core.privateapi.jsonstream.IJsonStreamApiComponent;
import neatlogic.framework.restful.core.privateapi.metric.IMetricApiComponent;
import neatlogic.framework.restful.core.privateapi.raw.IRawApiComponent;
import neatlogic.framework.restful.core.privateapi.sse.IPrivateSseApiComponent;
import neatlogic.framework.restful.dao.mapper.ApiMapper;
import neatlogic.framework.restful.dto.ApiHandlerVo;
import neatlogic.framework.restful.dto.ApiVo;
import neatlogic.framework.restful.enums.ApiType;
import neatlogic.framework.restful.ratelimiter.RateLimiterTokenBucket;
import neatlogic.framework.util.$;
import neatlogic.framework.util.HttpRequestUtil;
import neatlogic.framework.util.mongodb.IJsonSerializer;
import neatlogic.module.framework.restful.counter.ApiAccessCountService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.HandlerMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Objects;
import java.util.Set;


@Controller
@RequestMapping("/api/")
public class ApiDispatcher {
    static Logger logger = LoggerFactory.getLogger(ApiDispatcher.class);

    @Resource
    private ApiMapper apiMapper;

    @Resource
    private ApiAccessCountService apiAccessCountService;

    /*
      给fastJson加载自定义序列化配置，序列化json时返回正确格式
     */
    static {
        Reflections reflections = new Reflections("neatlogic");
        Set<Class<? extends IJsonSerializer>> enumClasses = reflections.getSubTypesOf(IJsonSerializer.class);
        for (Class<? extends IJsonSerializer> c : enumClasses) {
            try {
                SerializeConfig serializeConfig = SerializeConfig.getGlobalInstance();
                IJsonSerializer serializer = c.newInstance();
                serializeConfig.put(serializer.getType(), serializer);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }


    private void doIt(HttpServletRequest request, HttpServletResponse response, String token, ApiType apiType, JSONObject paramObj, JSONObject returnObj, String action) throws Exception {
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
        if (paramObj == null) {
            paramObj = new JSONObject();
        }
        RequestContext.get().setParam(JSON.toJSONString(paramObj, SerializerFeature.PrettyFormat));
        ApiVo interfaceVo = PrivateApiComponentFactory.getApiByToken(token);
        ApiVo dbApiVo = getDbApiVo(token, interfaceVo);
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
            IApiComponent restComponent = PrivateApiComponentFactory.getComponent(interfaceVo.getHandler(), ApiType.OBJECT, IApiComponent.class);
            FieldValidResultVo validResultVo = restComponent.doValid(interfaceVo, paramObj, validField);
            if (StringUtils.isNotBlank(validResultVo.getMsg())) {
                response.setStatus(ResponseCode.API_FIELD_INVALID.getCode());
                returnObj.put("Message", validResultVo.getMsg());
                if (validResultVo.getParam() != null) {
                    returnObj.put("Param", validResultVo.getParam());
                }
            }
            returnObj.put("Status", validResultVo.getStatus());
        } else {
            if (apiType.equals(ApiType.OBJECT)) {
                IApiComponent restComponent = PrivateApiComponentFactory.getComponent(interfaceVo.getHandler(), ApiType.OBJECT, IApiComponent.class);
                if (restComponent != null) {
                    if (action.equals("doservice")) {
                        /* 统计接口访问次数 */
                        apiAccessCountService.putToken(token);
                        Long starttime = System.currentTimeMillis();
                        Object returnV = restComponent.doService(interfaceVo, paramObj, response);
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
            } else if (apiType.equals(ApiType.STREAM)) {
                IJsonStreamApiComponent restComponent = PrivateApiComponentFactory.getComponent(interfaceVo.getHandler(), ApiType.STREAM, IJsonStreamApiComponent.class);
                if (restComponent != null) {
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
                            if (restComponent.disableReturnCircularReferenceDetect()) {
                                returnObj.put("_disableDetect", true);
                            }
                        } else {
                            returnObj.putAll(JSON.parseObject(JSON.toJSONString(returnV)));
                        }
                    } else {
                        returnObj.putAll(restComponent.help());
                    }
                } else {
                    throw new ComponentNotFoundException("接口组件:" + interfaceVo.getHandler() + "不存在");
                }
            } else if (apiType.equals(ApiType.BINARY)) {
                IBinaryStreamApiComponent restComponent = PrivateApiComponentFactory.getComponent(interfaceVo.getHandler(), ApiType.BINARY, IBinaryStreamApiComponent.class);
                if (restComponent != null) {
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
                            if (restComponent.disableReturnCircularReferenceDetect()) {
                                returnObj.put("_disableDetect", true);
                            }
                        } else {
                            returnObj.putAll(JSON.parseObject(JSON.toJSONString(returnV)));
                        }
                    } else {
                        returnObj.putAll(restComponent.help());
                    }
                } else {
                    throw new ComponentNotFoundException("接口组件:" + interfaceVo.getHandler() + "不存在");
                }
            } else if (apiType.equals(ApiType.SSE)) {
                IPrivateSseApiComponent restComponent = PrivateApiComponentFactory.getComponent(interfaceVo.getHandler(), ApiType.SSE, IPrivateSseApiComponent.class);
                if (restComponent != null) {
                    if (action.equals("doservice")) {
                        apiAccessCountService.putToken(token);
                        restComponent.doService(interfaceVo, paramObj, request, response);
                    } else {
                        returnObj.putAll(restComponent.help());
                    }
                } else {
                    throw new ComponentNotFoundException("接口组件:" + interfaceVo.getHandler() + "不存在");
                }
            } else if (apiType.equals(ApiType.METRIC)) {
                IMetricApiComponent restComponent = PrivateApiComponentFactory.getComponent(interfaceVo.getHandler(), ApiType.METRIC, IMetricApiComponent.class);
                if (restComponent != null) {
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
                            returnObj.put("requestSqlAudit", RequestContext.get().getRequestSqlAuditVo());
                            if (restComponent.disableReturnCircularReferenceDetect()) {
                                returnObj.put("_disableDetect", true);
                            }
                        } else {
                            returnObj.putAll(JSON.parseObject(JSON.toJSONString(returnV)));
                        }
                    } else {
                        returnObj.putAll(restComponent.help());
                    }
                } else {
                    throw new ComponentNotFoundException("接口组件:" + interfaceVo.getHandler() + "不存在");
                }
            } else if (apiType.equals(ApiType.RAW)) {
                IRawApiComponent restComponent = PrivateApiComponentFactory.getComponent(interfaceVo.getHandler(), ApiType.RAW, IRawApiComponent.class);
                if (restComponent != null) {
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

    /**
     * 动态路径接口请求进来时 token 可能已经替换成实际值，例如 report/show/123。
     * 数据库存的是规范 token（例如 report/show/{id}），这里需要回退到规范 token 再取配置，
     * 否则 needAudit / qps 等开关不会生效。
     */
    private ApiVo getDbApiVo(String requestToken, ApiVo interfaceVo) {
        ApiVo dbApiVo = apiMapper.getApiByToken(requestToken);
        if (dbApiVo == null && interfaceVo != null && StringUtils.isNotBlank(interfaceVo.getToken())
                && !Objects.equals(interfaceVo.getToken(), requestToken)) {
            dbApiVo = apiMapper.getApiByToken(interfaceVo.getToken());
        }
        return dbApiVo;
    }

    @GetMapping(value = "/rest/**")
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
            logger.error(ex.getMessage(), ex);
            response.setStatus(ResponseCode.EXCEPTION.getCode());
            returnObj.put("Status", "ERROR");
            returnObj.put("Message", ExceptionUtils.getStackFrames(ex));
        }
        if (!response.isCommitted()) {
            response.setContentType(Config.RESPONSE_TYPE_JSON);
            if (returnObj.containsKey("_disableDetect")) {
                returnObj.remove("_disableDetect");
                response.getWriter().print(returnObj.toString(SerializerFeature.DisableCircularReferenceDetect));
            } else {
                response.getWriter().print(returnObj);
            }
        }
    }

    @PostMapping(value = "/raw/**")
    public void dispatcherForRawPost(@RequestBody String jsonStr, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String pattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String token = new AntPathMatcher().extractPathWithinPattern(pattern, request.getServletPath());
        JSONObject returnObj = new JSONObject();
        try {
            //由于统一接受json参数，先封装后拆解
            JSONObject jsonObj = new JSONObject();
            if (jsonStr.startsWith("\"") && jsonStr.endsWith("\"")) {
                jsonStr = jsonStr.substring(1, jsonStr.length() - 1);
            }
            jsonObj.put("payload", jsonStr);
            doIt(request, response, token, ApiType.RAW, jsonObj, returnObj, "doservice");
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

    @PostMapping(value = "/fetch/**")
    public void dispatcherForFetchPost(@RequestBody String jsonStr, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String token = new AntPathMatcher().extractPathWithinPattern(pattern, request.getServletPath());
        JSONObject returnObj = new JSONObject();
        // ---------- 改为 fetch 可读流 ----------
        response.setContentType("text/plain;charset=UTF-8");
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Connection", "keep-alive");

        PrintWriter writer = response.getWriter();

        try {
            JSONObject paramObj;
            if (StringUtils.isNotBlank(jsonStr)) {
                try {
                    paramObj = JSON.parseObject(jsonStr);
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
            // 主执行逻辑，可在内部多次写出
            doIt(request, response, token, ApiType.OBJECT, paramObj, returnObj, "doservice");

        } catch (ApiRuntimeException ex) {
            response.setStatus(ResponseCode.API_RUNTIME.getCode());
            if (logger.isWarnEnabled()) {
                logger.warn(ex.getMessage(), ex);
            }
            writer.write("Error:" + ex.getMessage());
        } catch (NotFoundEditTargetException ex) {
            response.setStatus(ResponseCode.EDIT_TARGET_NOTFOUND.getCode());
            if (logger.isWarnEnabled()) {
                logger.warn(ex.getMessage(), ex);
            }
            writer.write("Error:" + ex.getMessage());
        } catch (PermissionDeniedException ex) {
            response.setStatus(ResponseCode.PERMISSION_DENIED.getCode());
            if (logger.isWarnEnabled()) {
                logger.warn(ex.getMessage(), ex);
            }
            writer.write("Error:" + $.t(ex.getMessage(), ex.getValues()));
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            response.setStatus(ResponseCode.EXCEPTION.getCode());
            writer.write("Error:" + Arrays.toString(ExceptionUtils.getStackFrames(ex)));
        } finally {
            writer.flush();
            writer.close();
        }
    }


    @PostMapping(value = "/rest/**")
    public void dispatcherForPost(@RequestBody String jsonStr, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String pattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String token = new AntPathMatcher().extractPathWithinPattern(pattern, request.getServletPath());
        JSONObject returnObj = new JSONObject();
        try {
            JSONObject paramObj;
            if (StringUtils.isNotBlank(jsonStr)) {
                try {
                    paramObj = JSON.parseObject(jsonStr);
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
        } catch (Throwable ex) {
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

    @PostMapping(value = "/stream/**")
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
            if (returnObj.containsKey("_disableDetect")) {
                returnObj.remove("_disableDetect");
                response.getWriter().print(returnObj.toString(SerializerFeature.DisableCircularReferenceDetect));
            } else {
                response.getWriter().print(returnObj.toJSONString());
            }
        }
    }

    @GetMapping(value = "/sse/**")
    public void dispatcherForGetSse(HttpServletRequest request, HttpServletResponse response) throws IOException {
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
            doIt(request, response, token, ApiType.SSE, paramObj, returnObj, "doservice");
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

    @PostMapping(value = "/sse/**")
    public void dispatcherForPostSse(@RequestBody(required = false) String jsonStr, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String token = new AntPathMatcher().extractPathWithinPattern(pattern, request.getServletPath());
        JSONObject returnObj = new JSONObject();
        try {
            JSONObject paramObj;
            if (StringUtils.isNotBlank(jsonStr)) {
                try {
                    paramObj = JSON.parseObject(jsonStr);
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
            doIt(request, response, token, ApiType.SSE, paramObj, returnObj, "doservice");
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
        } catch (Throwable ex) {
            response.setStatus(ResponseCode.EXCEPTION.getCode());
            returnObj.put("Status", "ERROR");
            returnObj.put("Message", ExceptionUtils.getStackTrace(ex));
            logger.error(ex.getMessage(), ex);
        }
        if (!response.isCommitted()) {
            response.setContentType(Config.RESPONSE_TYPE_JSON);
            response.getWriter().print(returnObj.toJSONString());
        }
    }

    @GetMapping(value = "/binary/**")
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
            if (UserContext.get() != null) {
                HttpRequestUtil.resetResponse(response);
            }
            response.setContentType(Config.RESPONSE_TYPE_JSON);
            if (returnObj.containsKey("_disableDetect")) {
                returnObj.remove("_disableDetect");
                response.getWriter().print(returnObj.toString(SerializerFeature.DisableCircularReferenceDetect));
            } else {
                response.getWriter().print(returnObj.toJSONString());
            }
        }
    }

    @GetMapping(value = "/metrics/**")
    public void dispatcherForGetMetric(HttpServletRequest request, HttpServletResponse response) throws IOException {
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
            doIt(request, response, token, ApiType.METRIC, paramObj, returnObj, "doservice");
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
            if (UserContext.get() != null) {
                HttpRequestUtil.resetResponse(response);
            }
            response.setContentType(Config.RESPONSE_TYPE_JSON);
            if (returnObj.containsKey("_disableDetect")) {
                returnObj.remove("_disableDetect");
                response.getWriter().print(returnObj.toString(SerializerFeature.DisableCircularReferenceDetect));
            } else {
                response.getWriter().print(returnObj.toJSONString());
            }
        }
    }

    @PostMapping(value = "/binary/**", consumes = "application/json")
    public void dispatcherForPostBinaryJson(@RequestBody String jsonStr, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String token = new AntPathMatcher().extractPathWithinPattern(pattern, request.getServletPath());
        JSONObject paramObj;
//        Enumeration<String> headerNames = request.getHeaderNames();
//        while (headerNames.hasMoreElements()) {
//            String name = headerNames.nextElement();
//            String value = request.getHeader(name);
//            System.out.println(name + "=" + value);
//        }
        if (StringUtils.isNotBlank(jsonStr)) {
            try {
                paramObj = JSON.parseObject(jsonStr);
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
        JSONObject returnObj = new JSONObject();
        try {
            doIt(request, response, token, ApiType.BINARY, paramObj, returnObj, "doservice");
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
            if (UserContext.get() != null) {
                HttpRequestUtil.resetResponse(response);
            }
            response.setContentType(Config.RESPONSE_TYPE_JSON);
            if (returnObj.containsKey("_disableDetect")) {
                returnObj.remove("_disableDetect");
                response.getWriter().print(returnObj.toString(SerializerFeature.DisableCircularReferenceDetect));
            } else {
                response.getWriter().print(returnObj.toJSONString());
            }
        }
    }

    @PostMapping(value = "/binary/**", consumes = "multipart/form-data")
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

    @GetMapping(value = "/help/rest/**")
    public void resthelp(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String token = new AntPathMatcher().extractPathWithinPattern(pattern, request.getServletPath());
        JSONObject returnObj = new JSONObject();
        try {
            doIt(request, response, token, ApiType.OBJECT, null, returnObj, "help");
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
        response.setContentType(Config.RESPONSE_TYPE_JSON);
        response.getWriter().print(returnObj.toJSONString());
    }

    @GetMapping(value = "/help/raw/**")
    public void rawhelp(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String token = new AntPathMatcher().extractPathWithinPattern(pattern, request.getServletPath());
        JSONObject returnObj = new JSONObject();
        try {
            doIt(request, response, token, ApiType.RAW, null, returnObj, "help");
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
        response.setContentType(Config.RESPONSE_TYPE_JSON);
        response.getWriter().print(returnObj.toJSONString());
    }


    @GetMapping(value = "/help/stream/**")
    public void streamhelp(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String token = new AntPathMatcher().extractPathWithinPattern(pattern, request.getServletPath());
        JSONObject returnObj = new JSONObject();
        try {
            doIt(request, response, token, ApiType.STREAM, null, returnObj, "help");
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
        response.setContentType(Config.RESPONSE_TYPE_JSON);
        response.getWriter().print(returnObj.toJSONString());
    }

    @GetMapping(value = "/help/binary/**")
    public void binaryhelp(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String token = new AntPathMatcher().extractPathWithinPattern(pattern, request.getServletPath());
        JSONObject returnObj = new JSONObject();
        try {
            doIt(request, response, token, ApiType.BINARY, null, returnObj, "help");
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
        response.setContentType(Config.RESPONSE_TYPE_JSON);
        response.getWriter().print(returnObj.toJSONString());
    }

    @GetMapping(value = "/help/sse/**")
    public void ssehelp(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String token = new AntPathMatcher().extractPathWithinPattern(pattern, request.getServletPath());
        JSONObject returnObj = new JSONObject();
        try {
            doIt(request, response, token, ApiType.SSE, null, returnObj, "help");
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
        response.setContentType(Config.RESPONSE_TYPE_JSON);
        response.getWriter().print(returnObj.toJSONString());
    }

    @GetMapping(value = "/help/metrics/**")
    public void metrichelp(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String token = new AntPathMatcher().extractPathWithinPattern(pattern, request.getServletPath());
        JSONObject returnObj = new JSONObject();
        try {
            doIt(request, response, token, ApiType.METRIC, null, returnObj, "help");
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
        response.setContentType(Config.RESPONSE_TYPE_JSON);
        response.getWriter().print(returnObj.toJSONString());
    }

}
