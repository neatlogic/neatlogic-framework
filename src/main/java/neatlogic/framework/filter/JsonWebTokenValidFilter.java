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

package neatlogic.framework.filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.asynchronization.threadlocal.RequestContext;
import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.asynchronization.threadlocal.UserContext;
import neatlogic.framework.common.config.Config;
import neatlogic.framework.common.constvalue.ResponseCode;
import neatlogic.framework.common.util.TenantUtil;
import neatlogic.framework.dao.cache.UserSessionCache;
import neatlogic.framework.dao.mapper.UserSessionContentMapper;
import neatlogic.framework.dao.mapper.UserSessionMapper;
import neatlogic.framework.dto.AuthenticationInfoVo;
import neatlogic.framework.dto.JwtVo;
import neatlogic.framework.dto.UserSessionVo;
import neatlogic.framework.dto.UserVo;
import neatlogic.framework.exception.core.ApiRuntimeException;
import neatlogic.framework.exception.user.UserNotFoundException;
import neatlogic.framework.filter.core.ILoginAuthHandler;
import neatlogic.framework.filter.core.LoginAuthFactory;
import neatlogic.framework.login.core.ILoginPostProcessor;
import neatlogic.framework.login.core.LoginPostProcessorFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.Date;

public class JsonWebTokenValidFilter extends OncePerRequestFilter {
    @Resource
    private UserSessionMapper userSessionMapper;

    @Resource
    private UserSessionContentMapper userSessionContentMapper;

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

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException {
        Cookie[] cookies = request.getCookies();
        String timezone = "+8:00";
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
                    timezone = (URLDecoder.decode(cookie.getValue(), "UTF-8"));
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
                returnErrorResponseJson(ResponseCode.TENANT_NOTFOUND, response, loginAuth.directUrl(), tenant);
                return;
            }
            TenantContext.init();
            TenantContext.get().switchTenant(tenant);
            logger.debug("======= defaultLoginAuth: ");
            //先按 default 认证，不存在才根据具体 AuthType 认证用户
            userVo = defaultLoginAuth.auth(cachedRequest, response);
            if (userVo != null) {
                logger.debug("======= getUser succeed: " + userVo.getUuid());
                isExpired = userExpirationValid(userVo, timezone, request, response);
                //用户如果过期则抛弃
                if (isExpired) {
                    userVo = null;
                }
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
                logger.info("AuthType: " + authType);
                if (StringUtils.isNotBlank(authType)) {
                    loginAuth = LoginAuthFactory.getLoginAuth(authType);
                    if (loginAuth != null) {
                        userVo = loginAuth.auth(cachedRequest, response);
                        if (userVo != null && StringUtils.isNotBlank(userVo.getUuid())) {
                            logger.debug("======= getUser succeed: " + userVo.getUuid());
                            isExpired = userExpirationValid(userVo, timezone, request, response);
                            if (!isExpired) {
                                for (ILoginPostProcessor loginPostProcessor : LoginPostProcessorFactory.getLoginPostProcessorSet()) {
                                    loginPostProcessor.loginAfterInitialization();
                                }
                            } else {
                                returnErrorResponseJson(ResponseCode.LOGIN_EXPIRED, response, loginAuth.directUrl());
                                return;
                            }
                        } else {
                            returnErrorResponseJson(ResponseCode.AUTH_FAILED, response, loginAuth.directUrl(), loginAuth.getType());
                            return;
                        }
                    } else {
                        returnErrorResponseJson(ResponseCode.AUTH_TYPE_NOTFOUND, response, defaultLoginAuth.directUrl(), authType);
                        return;
                    }
                } else {
                    returnErrorResponseJson(ResponseCode.AUTH_FAILED, response, defaultLoginAuth.directUrl(), loginAuth.getType());
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
                returnErrorResponseJson(ResponseCode.API_RUNTIME, response, false, ex.getMessage());
            }
        } catch (UserNotFoundException ex) {
            logger.error(ex.getMessage(), ex);
            try {
                // 不返回跳转地址，直接跳转到显示错误信息页面
                returnErrorResponseJson(ResponseCode.AUTH_FAILED, response, null, ex.getMessage());
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                throw new ApiRuntimeException(e);
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            try {
                returnErrorResponseJson(ResponseCode.AUTH_FAILED, response, loginAuth != null ? loginAuth.directUrl() : defaultLoginAuth.directUrl(), ex.getMessage());
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
     * @param directUrl    前端跳转url
     * @param args         异常码构造入参
     * @throws IOException 异常
     */
    private void returnErrorResponseJson(ResponseCode responseCode, HttpServletResponse response, String directUrl, boolean isRemoveCookie, Object... args) throws Exception {
        JSONObject redirectObj = new JSONObject();
        String message = responseCode.getMessage(args);
        redirectObj.put("Status", "FAILED");
        redirectObj.put("Message", message);
        logger.debug("======login error:" + message);
        response.setStatus(responseCode.getCode());
        redirectObj.put("DirectUrl", directUrl);
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
     * @param directUrl    前端跳转url
     * @param args         异常码构造入参
     * @throws IOException 异常
     */
    private void returnErrorResponseJson(ResponseCode responseCode, HttpServletResponse response, String directUrl, Object... args) throws Exception {
        returnErrorResponseJson(responseCode, response, directUrl, true, args);
    }

    /**
     * 返回异常JSON
     *
     * @param responseCode 异常码
     * @param response     相应
     * @param args         异常码构造入参
     * @throws IOException 异常
     */
    private void returnErrorResponseJson(ResponseCode responseCode, HttpServletResponse response, boolean isRemoveCookie, Object... args) throws Exception {
        returnErrorResponseJson(responseCode, response, null, isRemoveCookie, args);
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
     *
     * @return 不超时返回权限信息，否则返回null
     */
    private boolean userExpirationValid(UserVo userVo, String timezone, HttpServletRequest request, HttpServletResponse response) {
        JwtVo jwt = userVo.getJwtVo();
        Object authenticationInfoStr = UserSessionCache.getItem(jwt.getTokenHash());
        if (authenticationInfoStr == null) {
            UserSessionVo userSessionVo = userSessionMapper.getUserSessionByTokenHash(jwt.getTokenHash());
            if (null != userSessionVo && (jwt.validTokenCreateTime(userSessionVo.getTokenCreateTime()))) {
                Date visitTime = userSessionVo.getSessionTime();
                Date now = new Date();
                int expire = Config.USER_EXPIRETIME();
                long expireTime = expire * 60L * 1000L + visitTime.getTime();
                if (now.getTime() < expireTime) {
                    String authInfo = userSessionContentMapper.getUserSessionContentByHash(userSessionVo.getAuthInfoHash());
                    userSessionVo.setAuthInfoStr(authInfo);
                    userSessionMapper.updateUserSessionTimeByTokenHash(jwt.getTokenHash());
                    AuthenticationInfoVo authenticationInfo = userSessionVo.getAuthInfo();
                    UserSessionCache.addItem(jwt.getTokenHash(), JSON.toJSONString(authenticationInfo));
                    UserContext.init(userVo, authenticationInfo, timezone, request, response);
                    return false;
                }
                userSessionMapper.deleteUserSessionByTokenHash(jwt.getTokenHash());
            }
        } else {
            AuthenticationInfoVo authenticationInfoVo = JSON.toJavaObject(JSON.parseObject(authenticationInfoStr.toString()), AuthenticationInfoVo.class);
            UserContext.init(userVo, authenticationInfoVo, timezone, request, response);
            return false;
        }
        return true;
    }


}
