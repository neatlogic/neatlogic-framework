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

import neatlogic.framework.asynchronization.threadlocal.InputFromContext;
import neatlogic.framework.asynchronization.threadlocal.RequestContext;
import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.asynchronization.threadlocal.UserContext;
import neatlogic.framework.common.config.Config;
import neatlogic.framework.common.constvalue.InputFrom;
import neatlogic.framework.common.constvalue.SystemUser;
import neatlogic.framework.common.util.TenantUtil;
import neatlogic.framework.dao.mapper.UserMapper;
import neatlogic.framework.dto.UserVo;
import neatlogic.framework.exception.core.ApiRuntimeException;
import neatlogic.framework.exception.integration.AuthenticateException;
import neatlogic.framework.exception.tenant.TenantNotFoundException;
import neatlogic.framework.exception.type.ApiNotFoundException;
import neatlogic.framework.exception.type.ComponentNotFoundException;
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
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONReader;
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
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/public/api/")
public class PublicApiDispatcher {
    Logger logger = LoggerFactory.getLogger(PublicApiDispatcher.class);

    @Resource
    private ApiMapper apiMapper;


    @Resource
    UserMapper userMapper;

    private static final Map<Integer, String> errorMap = new HashMap<>();

    public PublicApiDispatcher() {
        errorMap.put(408, "请求已超时");
        errorMap.put(400, "由于包含语法错误，当前请求无法被服务器理解");
        errorMap.put(412, "请求头缺少认证信息");
        errorMap.put(410, "服务不可用");
        errorMap.put(401, "用户验证失败");
        errorMap.put(403, "禁止访问");
        errorMap.put(470, "访问频率过高，请稍后访问");
        errorMap.put(521, "访问频率过高，请稍后访问");
    }

    private void doIt(HttpServletRequest request, HttpServletResponse response, String token, ApiType apiType, JSONObject paramObj, JSONObject returnObj, String action) throws Exception {
        InputFromContext.init(InputFrom.RESTFUL);
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
                UserContext.init(userVo, timezone, request, response);
            }
        }
        if(userVo == null){
            UserContext.init(SystemUser.SYSTEM.getUserVo(), SystemUser.SYSTEM.getTimezone());
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
            response.setStatus(429);
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
                int result = apiAuth.auth(interfaceVo, paramObj, request);
                if (result != 1) {
                    throw new AuthenticateException(errorMap.get(result));
                }
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
        RequestContext.init(request, token);
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
        }catch (ApiRuntimeException ex) {
            response.setStatus(520);
            returnObj.put("Status", "ERROR");
            returnObj.put("Message", ex.getMessage());
        } catch (PermissionDeniedException ex) {
            response.setStatus(523);
            returnObj.put("Status", "ERROR");
            returnObj.put("Message", ex.getMessage());
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            response.setStatus(500);
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
        RequestContext.init(request, token);
        JSONObject returnObj = new JSONObject();
        try {
            JSONObject paramObj;
            if (StringUtils.isNotBlank(jsonStr)) {
                try {
                    paramObj = JSONObject.parseObject(jsonStr);
                } catch (Exception e) {
                    throw new ApiRuntimeException("请求参数需要符合JSON格式");
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
        }catch (ApiRuntimeException ex) {
            response.setStatus(520);
            returnObj.put("Status", "ERROR");
            returnObj.put("Message", ex.getMessage());
        } catch (PermissionDeniedException ex) {
            response.setStatus(523);
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
            response.getWriter().print(returnObj);
        }
    }

    @RequestMapping(value = "/stream/**", method = RequestMethod.POST)
    public void dispatcherForPostStream(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String token = new AntPathMatcher().extractPathWithinPattern(pattern, request.getServletPath());
        RequestContext.init(request, token);
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
        }catch (TenantNotFoundException ex) {
            logger.error(ex.getMessage(), ex);
            response.setStatus(521);
            returnObj.put("Status", "ERROR");
            returnObj.put("Message", ExceptionUtils.getStackFrames(ex));
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
            response.setStatus(500);
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
        RequestContext.init(request, token);
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
        }catch (ApiRuntimeException ex) {
            response.setStatus(520);
            returnObj.put("Status", "ERROR");
            returnObj.put("Message", ex.getMessage());
        } catch (PermissionDeniedException ex) {
            response.setStatus(523);
            returnObj.put("Status", "ERROR");
            returnObj.put("Message", ex.getMessage());
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            response.setStatus(500);
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
        RequestContext.init(request, token);
        JSONObject paramObj;
        if (StringUtils.isNotBlank(jsonStr)) {
            try {
                paramObj = JSONObject.parseObject(jsonStr);
            } catch (Exception e) {
                throw new ApiRuntimeException("请求参数需要符合JSON格式");
            }
        } else {
            paramObj = new JSONObject();
        }

        JSONObject returnObj = new JSONObject();
        try {
            doIt(request, response, token, ApiType.BINARY, paramObj, returnObj, "doservice");
        }catch (ApiRuntimeException ex) {
            response.setStatus(520);
            returnObj.put("Status", "ERROR");
            returnObj.put("Message", ex.getMessage());
        } catch (PermissionDeniedException ex) {
            response.setStatus(523);
            returnObj.put("Status", "ERROR");
            returnObj.put("Message", ex.getMessage());
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            response.setStatus(500);
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
        RequestContext.init(request, token);
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
        }catch (ApiRuntimeException ex) {
            response.setStatus(520);
            returnObj.put("Status", "ERROR");
            returnObj.put("Message", ex.getMessage());
        } catch (PermissionDeniedException ex) {
            response.setStatus(523);
            returnObj.put("Status", "ERROR");
            returnObj.put("Message", ex.getMessage());
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            response.setStatus(500);
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
        RequestContext.init(request, token);
        JSONObject returnObj = new JSONObject();
        try {
            doIt(request, response, token, ApiType.OBJECT, null, returnObj, "help");
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
            response.setStatus(500);
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
        RequestContext.init(request, token);
        JSONObject returnObj = new JSONObject();
        try {
            doIt(request, response, token, ApiType.STREAM, null, returnObj, "help");
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
            response.setStatus(500);
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
        RequestContext.init(request, token);
        JSONObject returnObj = new JSONObject();
        try {
            doIt(request, response, token, ApiType.BINARY, null, returnObj, "help");
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
            response.setStatus(500);
            returnObj.put("Status", "ERROR");
            returnObj.put("Message", ExceptionUtils.getStackFrames(ex));
        }
        response.setContentType(Config.RESPONSE_TYPE_JSON);
        response.getWriter().print(returnObj.toJSONString());
    }

}
