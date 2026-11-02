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

package neatlogic.framework.filter;

import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.asynchronization.threadlocal.RequestContext;
import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.asynchronization.threadlocal.UserContext;
import neatlogic.framework.common.config.Config;
import neatlogic.framework.common.constvalue.ResponseCode;
import neatlogic.framework.common.util.TenantUtil;
import neatlogic.framework.dao.mapper.UserSessionContentMapper;
import neatlogic.framework.dao.mapper.UserSessionMapper;
import neatlogic.framework.dto.AuthenticationInfoVo;
import neatlogic.framework.dto.JwtVo;
import neatlogic.framework.dto.UserSessionVo;
import neatlogic.framework.dto.UserVo;
import neatlogic.framework.exception.core.ApiRuntimeException;
import neatlogic.framework.exception.user.UserPasswordExpiredException;
import neatlogic.framework.filter.core.ILoginAuthHandler;
import neatlogic.framework.filter.request.RequestBodyResources;
import neatlogic.framework.filter.request.RequestMultipartResolver;
import neatlogic.framework.filter.request.RequestBodyErrorHandler;
import neatlogic.framework.exception.request.RequestBodyException;
import neatlogic.framework.filter.core.LoginAuthFactory;
import neatlogic.framework.service.LoginService;
import neatlogic.framework.util.UserTimezoneResolver;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Objects;

public class JsonWebTokenValidFilter extends OncePerRequestFilter {
    //前端定时心跳的时间
    public static final int WEB_HEARTBEAT_INTERVAL = 60;

    public static final String DEFAULT_AUTH_TYPE = "default";

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    @Resource(name = "multipartResolver")
    private RequestMultipartResolver multipartResolver;

    @Resource
    private UserSessionMapper userSessionMapper;

    @Resource
    private UserSessionContentMapper userSessionContentMapper;

    @Resource
    private LoginService loginService;

    /**
     * Default constructor.
     */
    public JsonWebTokenValidFilter() {
    }

    /**
     * @see Filter#destroy()
     */
    @Override
    public void destroy() {
    }

    /**
     * MCP 接口由 McpDispatcher 独立处理租户和认证，避免普通登录认证链提前拦截请求。
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String contextPath = StringUtils.defaultString(request.getContextPath());
        return request.getRequestURI().startsWith(contextPath + "/api/mcp/");
    }

    /**
     * 确定租户后解析请求时区，并在各认证分支完成后统一写入用户上下文。
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException {
        //是否已过期
        boolean isExpired = false;
        UserVo userVo = null;
        //默认认证通过的用户
        UserVo defaultAuthUser;
        String authType = DEFAULT_AUTH_TYPE;
        ILoginAuthHandler defaultLoginAuth = LoginAuthFactory.getLoginAuth(authType);
        ILoginAuthHandler loginAuth = defaultLoginAuth;
        //初始化request上下文
        RequestContext.init(request, request.getRequestURI(), response);

        RequestBodyResources bodyResources = null;
        //判断租户
        try {
            String tenant = request.getHeader("Tenant");
            if (!TenantUtil.hasTenant(tenant)) {
                returnErrorResponseJson(ResponseCode.TENANT_NOTFOUND, response, loginAuth, tenant);
                return;
            }
            // 租户不存在时不接收请求体；资源限制在请求创建时固定。
            bodyResources = new RequestBodyResources(request, multipartResolver);
            HttpServletRequest cachedRequest = bodyResources.forAuthentication(defaultLoginAuth.supportsParsedMultipart());
            RequestContext.get().setRequest(cachedRequest);
            TenantContext.init();
            TenantContext.get().switchTenant(tenant);
            String timezone = UserTimezoneResolver.getRequestTimezone(request);
            logger.debug("======= defaultLoginAuth: ");
            //先按 default 认证，不存在才根据具体 AuthType 认证用户
            try {
                defaultAuthUser = defaultLoginAuth.auth(cachedRequest, response, timezone);
                if (defaultAuthUser != null) {
                    logger.debug("======= getUser succeed: " + defaultAuthUser.getUuid());
                    UserSessionVo userSessionVo = userSessionMapper.getUserSessionByTokenHash(defaultAuthUser.getJwtVo().getTokenHash());
                    isExpired = userExpirationValid(userSessionVo, defaultAuthUser.getJwtVo(), defaultAuthUser);
                    if (!isExpired) {
                        initUserContext(userSessionVo, defaultAuthUser, timezone);
                        userVo = defaultAuthUser;
                    }else{
                        logger.debug("======= default auth is expired: " + defaultAuthUser.getUuid() + ". next step to check other authType");
                    }
                }
            } catch (UserPasswordExpiredException e) {
                returnErrorResponseJson(ResponseCode.PASSWORD_EXPIRED, response, false, defaultLoginAuth, e.getMessage());
                return;
            }
            //default认证获取不到有效用户
            if (userVo == null) {
                //获取认证插件名,优先使用请求方指定的认证
                String authTypeHeader = request.getHeader("AuthType");
                if (StringUtils.isNotBlank(authTypeHeader)) {
                    authType = authTypeHeader;
                } else {
                    authType = Config.LOGIN_AUTH_TYPE();
                }
                logger.debug("AuthType: " + authType);
                if (StringUtils.isNotBlank(authType) && !DEFAULT_AUTH_TYPE.equalsIgnoreCase(authType)) {
                    loginAuth = LoginAuthFactory.getLoginAuth(authType);
                    if (loginAuth != null) {
                        cachedRequest = bodyResources.forAuthentication(loginAuth.supportsParsedMultipart());
                        RequestContext.get().setRequest(cachedRequest);
                        userVo = loginAuth.auth(cachedRequest, response, timezone);
                        if (userVo != null && StringUtils.isNotBlank(userVo.getUuid())) {
                            logger.debug("======= getUser succeed: " + userVo.getUuid());
                        } else {
                            String authFailMessage = String.format("%s认证失败，原因：%s", loginAuth.getType(), userVo == null ? "认证插件未返回用户，请检查认证信息是否正确" : "认证插件返回的用户缺少uuid");
                            returnErrorResponseJson(ResponseCode.AUTH_FAILED, response, loginAuth, authFailMessage);
                            return;
                        }
                    } else {
                        returnErrorResponseJson(ResponseCode.AUTH_TYPE_NOTFOUND, response, defaultLoginAuth, authType);
                        return;
                    }
                } else {
                    //无需第三方认证，则按默认认证结果返回
                    if (isExpired) {
                        loginService.logout(defaultAuthUser.getJwtVo());
                        if (logger.isDebugEnabled()) {
                            logger.debug(defaultAuthUser.getUserName() + "(" + defaultAuthUser.getUserId() + ")用户超时登出");
                        }
                        returnErrorResponseJson(ResponseCode.LOGIN_EXPIRED, response, defaultLoginAuth, loginAuth.getType());
                    } else {
                        returnErrorResponseJson(ResponseCode.AUTH_FAILED, response, defaultLoginAuth, loginAuth.getType());
                    }
                    return;
                }
            } else {
                logger.debug("======= getUser succeed: " + userVo.getUuid());
            }

            // 认证结束后切换为下游使用的请求：非 multipart 且未建立缓存时，去掉认证缓存包装，保留大小限制，
            // 避免普通 JWT 请求仅由 Spring/业务读取一次 body 也触发缓存；已缓存的请求则保留重放能力。
            // multipart 在此取得共享解析结果，供后续路径过滤器检查字段，Dispatcher 和业务复用字段及文件。
            cachedRequest = bodyResources.forDispatch();
            // 同步上下文中的请求引用，使通过 RequestContext 取请求的代码与 filterChain 收到同一对象，
            // 避免继续访问认证阶段的旧包装而误触发缓存，或无法获取共享 multipart 字段和文件；此处不复制 body。
            RequestContext.get().setRequest(cachedRequest);
            try {
                filterChain.doFilter(cachedRequest, response);
                if (bodyResources.getFailure() != null && !response.isCommitted()) {
                    RequestBodyErrorHandler.write(response, bodyResources.getFailure());
                }
            } catch (Exception ex) {
                // 请求资源错误可能被 Spring 包装，仍须保留 413 等状态，不能统一转换为 520。
                logger.error(ex.getMessage(), ex);
                RequestBodyException bodyError = RequestBodyErrorHandler.find(request, ex);
                if (bodyError != null) { throw bodyError; }
                //兼容“处理response,对象toString可能会异常”的场景，过了filter，应该是520异常
                returnErrorResponseJson(ResponseCode.API_RUNTIME, response, false, loginAuth, ex.getMessage());
            }
        } catch (RequestBodyException ex) {
            logger.error(ex.getMessage(), ex);
            RequestBodyErrorHandler.write(response, ex);
        } catch (ApiRuntimeException ex) {
            logger.error(ex.getMessage(), ex);
            try {
                // 不返回跳转地址，直接到显示错误信息页面
                returnErrorResponseJson(false, ResponseCode.API_RUNTIME, response, loginAuth != null ? loginAuth : Objects.requireNonNull(defaultLoginAuth), ex, ex.getMessage());
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                throw new ApiRuntimeException(e);
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            try {
                returnErrorResponseJson(false, ResponseCode.EXCEPTION, response, loginAuth != null ? loginAuth : Objects.requireNonNull(defaultLoginAuth), ex, ex.getClass().getName() + ":" + ex.getMessage());
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                throw new ApiRuntimeException(e);
            }
        } finally {
            // 预解析的上传不属于 Dispatcher，所有提前返回和失败都由此释放。
            if (bodyResources != null) { bodyResources.close(); }
        }
    }

    /**
     * 返回异常JSON
     *
     * @param responseCode 异常码
     * @param response     相应
     * @param loginAuth    认证插件
     * @param args         异常码构造入参
     * @throws IOException 异常
     */
    private void returnErrorResponseJson(boolean isRemoveCookie, ResponseCode responseCode, HttpServletResponse response, ILoginAuthHandler loginAuth, Exception ex, Object... args) throws Exception {
        JSONObject redirectObj = new JSONObject();
        String message = responseCode.getMessage(args);
        redirectObj.put("Status", "FAILED");
        redirectObj.put("Message", message);
        logger.debug("======login error:" + message);
        response.setStatus(responseCode.getCode());
        redirectObj.put("DirectUrl", loginAuth.directUrl());
        redirectObj.put("IsAutoDirect", loginAuth.isAutoDirect());
        //最终导致认证失败的 authType
        redirectObj.put("authType", loginAuth.getType());
        if (ex != null) {
            redirectObj.put("stackTrace", ExceptionUtils.getStackFrames(ex));
        }
        if (isRemoveCookie) {
            removeAuthCookie(response);
        }
        response.setContentType(Config.RESPONSE_TYPE_JSON);
        response.getWriter().print(redirectObj.toJSONString());
    }

    /**
     * 返回异常JSON
     *
     * @param responseCode 异常码
     * @param response     相应
     * @param loginAuth    认证插件
     * @param args         异常码构造入参
     * @throws IOException 异常
     */
    private void returnErrorResponseJson(ResponseCode responseCode, HttpServletResponse response, ILoginAuthHandler loginAuth, Object... args) throws Exception {
        returnErrorResponseJson(true, responseCode, response, loginAuth, null, args);
    }

    /**
     * 返回异常JSON
     *
     * @param responseCode 异常码
     * @param response     相应
     * @param args         异常码构造入参
     * @throws IOException 异常
     */
    private void returnErrorResponseJson(ResponseCode responseCode, HttpServletResponse response, boolean isRemoveCookie, ILoginAuthHandler loginAuth, Object... args) throws Exception {
        returnErrorResponseJson(isRemoveCookie, responseCode, response, loginAuth, null, args);
    }

    /**
     * 登录异常后端清除neatlogic_authorization cookie，防止sso循环跳转
     */
    private void removeAuthCookie(HttpServletResponse response) {
        if (TenantContext.get() != null) {
            Cookie authCookie = new Cookie("neatlogic_authorization", null);
            authCookie.setPath("/" + TenantContext.get().getTenantUuid());
            authCookie.setMaxAge(0);//表示删除
            response.addCookie(authCookie);
        }
    }

    /**
     * 校验用户登录超时
     * - 用户没有点击或滚动鼠标超过会话有效期（默认 60分）
     * - 在别的浏览器登录同个用户默认是会互相挤退
     *
     * @return 不超时返回权限信息，否则返回null
     */
    private boolean userExpirationValid(UserSessionVo userSessionVo, JwtVo jwtVo, UserVo userVo) {
        if (userSessionVo == null) {
            if (logger.isDebugEnabled()) {
                logger.debug(userVo.getUserName() + "(" + userVo.getUserId() + ")用户没有找到上次访问记录，会话已超时或已被终止，请重新登录");
            }
            return true;
        }
        if (jwtVo.validTokenCreateTime(userSessionVo.getTokenCreateTime())) {
            Date visitTime = userSessionVo.getSessionTime();
            Date now = new Date();
            //超时时间需加上前端定时心跳间隔时间，否则在快超时的极限情况下用户又继续操作，又没到下次定时 heartbeat 会导致误登出
            long expireTime = (Config.USER_EXPIRETIME() * 60L + WEB_HEARTBEAT_INTERVAL) * 1000L + visitTime.getTime();
            //System.out.println(TimeUtil.millisecondsFormat(now.getTime(), 4, TimeUnit.SECONDS, " ") + "  now");
            //System.out.println(TimeUtil.millisecondsFormat(expireTime, 4, TimeUnit.SECONDS, " ") + "  expire");
            if (now.getTime() > expireTime) {
                userSessionMapper.deleteUserSessionByTokenHash(userSessionVo.getTokenHash());
                if (logger.isDebugEnabled()) {
                    logger.debug(userVo.getUserName() + "(" + userVo.getUserId() + ")用户上次访问时间为" + LocalDateTime.ofInstant(userSessionVo.getSessionTime().toInstant(), ZoneId.systemDefault()).format(dateTimeFormatter) + ", 登录有效时间为" + Config.USER_EXPIRETIME() + "分钟，会话已超时或已被终止，请重新登录");
                }
                return true;
            }
            return false;
        }
        if (logger.isDebugEnabled()) {
            logger.debug(userVo.getUserName() + "(" + userVo.getUserId() + ")用户同时在不同浏览器中登录，当前ENABLE_CONCURRENT_SESSION值为" + Config.ENABLE_CONCURRENT_SESSION());
        }
        return true;

    }

    /**
     * 跟新userSessionCache和用户上下文
     *
     * @param userSessionVo 最新用户回话信息（数据库）
     * @param userVo        用户
     * @param timezone      时区
     */
    private void initUserContext(UserSessionVo userSessionVo, UserVo userVo, String timezone) {
        String authInfo = userSessionContentMapper.getUserSessionContentByHash(userSessionVo.getAuthInfoHash());
        userSessionVo.setAuthInfoStr(authInfo);
        AuthenticationInfoVo authenticationInfoVo = userSessionVo.getAuthInfo();
        authenticationInfoVo.setUserUuid(userVo.getUuid());
        UserContext.init(userVo, authenticationInfoVo, timezone);
    }
}
