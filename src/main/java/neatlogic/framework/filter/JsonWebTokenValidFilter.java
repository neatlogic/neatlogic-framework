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
import neatlogic.framework.filter.core.LoginAuthFactory;
import neatlogic.framework.service.LoginService;
import neatlogic.framework.util.TimeUtil;
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
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.ZoneOffset;
import java.util.Date;

public class JsonWebTokenValidFilter extends OncePerRequestFilter {
    //前端定时心跳的时间
    public static final int WEB_HEARTBEAT_INTERVAL= 60;

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

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException {
        Cookie[] cookies = request.getCookies();
        String timezone = TimeUtil.ZONE_TIME;
        //是否已过期
        boolean isExpired = false;
        UserVo userVo;
        String authType = "default";
        ILoginAuthHandler defaultLoginAuth = LoginAuthFactory.getLoginAuth(authType);
        ILoginAuthHandler loginAuth = defaultLoginAuth;
        //获取时区
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("neatlogic_timezone".equals(cookie.getName())) {
                    String timezoneTmp = (URLDecoder.decode(cookie.getValue(), StandardCharsets.UTF_8));
                    try {
                        ZoneOffset.of(timezoneTmp);
                        timezone = timezoneTmp;
                    } catch (Exception ignored) {

                    }
                }
            }
        }
        //初始化request上下文
        RequestContext.init(request, request.getRequestURI(), response);

        //判断租户
        try {
            String tenant = request.getHeader("Tenant");
            //认证过程中可能需要从request中获取inputStream，为了后续spring也可以获取inputStream，需要做一层cached
            HttpServletRequest cachedRequest = new CachedBodyHttpServletRequest(request);
            if (!TenantUtil.hasTenant(tenant)) {
                returnErrorResponseJson(ResponseCode.TENANT_NOTFOUND, response, loginAuth, tenant);
                return;
            }
            TenantContext.init();
            TenantContext.get().switchTenant(tenant);
            logger.debug("======= defaultLoginAuth: ");
            //先按 default 认证，不存在才根据具体 AuthType 认证用户
            try {
                userVo = defaultLoginAuth.auth(cachedRequest, response);
                if (userVo != null) {
                    logger.debug("======= getUser succeed: " + userVo.getUuid());
                    UserSessionVo userSessionVo = userSessionMapper.getUserSessionByTokenHash(userVo.getJwtVo().getTokenHash());
                    isExpired = userExpirationValid(userSessionVo, userVo.getJwtVo());
                    //用户如果过期则抛弃
                    if (isExpired) {
                        logger.debug("======= login expired: " + userVo.getUuid());
                        loginService.logout(userVo.getJwtVo());
                        returnErrorResponseJson(ResponseCode.LOGIN_EXPIRED, response, defaultLoginAuth, loginAuth.getType());
                        return;
                    } else {
                        initUserContext(userSessionVo, userVo, timezone);
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
                if (StringUtils.isNotBlank(authType)) {
                    loginAuth = LoginAuthFactory.getLoginAuth(authType);
                    if (loginAuth != null) {
                        userVo = loginAuth.auth(cachedRequest, response);
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
                    returnErrorResponseJson(ResponseCode.AUTH_FAILED, response, defaultLoginAuth, "认证失败，原因：缺少认证类型，请检查请求头AuthType或租户登录认证配置");
                    return;
                }
            } else {
                logger.debug("======= getUser succeed: " + userVo.getUuid());
            }

            try {
                filterChain.doFilter(cachedRequest, response);
            } catch (Exception ex) {
                //兼容“处理response,对象toString可能会异常”的场景，过了filter，应该是520异常
                logger.error(ex.getMessage(), ex);
                returnErrorResponseJson(ResponseCode.API_RUNTIME, response, false, loginAuth != null ? loginAuth : defaultLoginAuth, ex.getMessage());
            }
        } catch (ApiRuntimeException ex) {
            logger.error(ex.getMessage(), ex);
            try {
                // 不返回跳转地址，直接到显示错误信息页面
                returnErrorResponseJson(false, ResponseCode.API_RUNTIME, response, loginAuth != null ? loginAuth : defaultLoginAuth, ex, ex.getMessage());
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                throw new ApiRuntimeException(e);
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            try {
                returnErrorResponseJson(false, ResponseCode.EXCEPTION, response, loginAuth != null ? loginAuth : defaultLoginAuth, ex, ex.getClass().getName() + ":" + ex.getMessage());
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                throw new ApiRuntimeException(e);
            }
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
    private boolean userExpirationValid(UserSessionVo userSessionVo, JwtVo jwtVo) {
        if (userSessionVo != null && (jwtVo.validTokenCreateTime(userSessionVo.getTokenCreateTime()))) {
            Date visitTime = userSessionVo.getSessionTime();
            Date now = new Date();
            //超时时间需加上前端定时心跳间隔时间，否则在快超时的极限情况下用户又继续操作，又没到下次定时 heartbeat 会导致误登出
            long expireTime = (Config.USER_EXPIRETIME() * 60L + WEB_HEARTBEAT_INTERVAL) * 1000L + visitTime.getTime();
            //System.out.println(TimeUtil.millisecondsFormat(now.getTime(), 4, TimeUnit.SECONDS, " ") + "  now");
            //System.out.println(TimeUtil.millisecondsFormat(expireTime, 4, TimeUnit.SECONDS, " ") + "  expire");
            if (now.getTime() > expireTime) {
                userSessionMapper.deleteUserSessionByTokenHash(userSessionVo.getTokenHash());
                return true;
            }
            return false;
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
