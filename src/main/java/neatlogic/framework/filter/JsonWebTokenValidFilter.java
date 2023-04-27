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

package neatlogic.framework.filter;

import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.asynchronization.threadlocal.UserContext;
import neatlogic.framework.common.config.Config;
import neatlogic.framework.common.util.TenantUtil;
import neatlogic.framework.dao.cache.UserSessionCache;
import neatlogic.framework.dao.mapper.UserSessionMapper;
import neatlogic.framework.dto.AuthenticationInfoVo;
import neatlogic.framework.dto.UserSessionVo;
import neatlogic.framework.dto.UserVo;
import neatlogic.framework.filter.core.ILoginAuthHandler;
import neatlogic.framework.filter.core.LoginAuthFactory;
import neatlogic.framework.login.core.ILoginPostProcessor;
import neatlogic.framework.login.core.LoginPostProcessorFactory;
import neatlogic.framework.service.AuthenticationInfoService;
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
    // private ServletContext context;
    @Resource
    private UserSessionMapper userSessionMapper;
    @Resource
    private AuthenticationInfoService authenticationInfoService;

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
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException {
        Cookie[] cookies = request.getCookies();
        String timezone = "+8:00";
        boolean isUnExpired = false;
        boolean hasTenant = false;
        UserVo userVo = null;
        JSONObject redirectObj = new JSONObject();
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
        //判断租户
        try {
            //logger.info("requestUrl:"+request.getRequestURI()+" ------------------------------------------------ start");
            String tenant = request.getHeader("Tenant");
            //认证过程中可能需要从request中获取inputStream，为了后续spring也可以获取inputStream，需要做一层cached
            HttpServletRequest cachedRequest = new CachedBodyHttpServletRequest(request);
            if (TenantUtil.hasTenant(tenant)) {
                hasTenant = true;
                TenantContext.init();
                TenantContext.get().switchTenant(tenant);
                logger.warn("======= defaultLoginAuth: ");
                //先按 default 认证，不存在才根据具体 AuthType 认证用户
                userVo = defaultLoginAuth.auth(cachedRequest, response);
                AuthenticationInfoVo authenticationInfoVo = null;
                if (userVo == null) {
                    authType = request.getHeader("AuthType");
                    logger.warn("======= AuthType: " + authType);
                    logger.info("AuthType: " + authType);
                    if (StringUtils.isNotBlank(authType)) {
                        loginAuth = LoginAuthFactory.getLoginAuth(authType);
                        if (loginAuth != null) {
                            userVo = loginAuth.auth(cachedRequest, response);
                            if (userVo != null && StringUtils.isNotBlank(userVo.getUuid())) {
                                logger.warn("======= userUuid: " + userVo.getUuid());
                                authenticationInfoVo = authenticationInfoService.getAuthenticationInfo(userVo.getUuid());
                                UserContext.init(userVo, authenticationInfoVo, timezone, request, response);
                                for (ILoginPostProcessor loginPostProcessor : LoginPostProcessorFactory.getLoginPostProcessorSet()) {
                                    loginPostProcessor.loginAfterInitialization();
                                }
                            }
                        }
                    }
                }
                if (userVo != null) {
                    if (authenticationInfoVo == null) {
                        authenticationInfoVo = authenticationInfoService.getAuthenticationInfo(userVo.getUuid());
                        UserContext.init(userVo, authenticationInfoVo, timezone, request, response);
                        logger.warn("======= getAuthenticationInfoService succeed: " + userVo.getUuid());
                    }
                    isUnExpired = userExpirationValid();
                    if (isUnExpired) {
                        //只要获取到用户就刷新用户访问时间，防止使用中的用户登出
                        userSessionMapper.updateUserSession(userVo.getUuid());
                    }
                }
            }

            if (hasTenant && userVo != null && isUnExpired) {
                filterChain.doFilter(cachedRequest, response);
            } else {
                if (!hasTenant) {
                    response.setStatus(521);
                    redirectObj.put("Status", "FAILED");
                    redirectObj.put("Message", "租户 '" + tenant + "' 不存在或已被禁用");
                    logger.warn("======= login error: 租户 '" + tenant + "' 不存在或已被禁用");
                } else if (userVo == null) {
                    response.setStatus(522);
                    redirectObj.put("Status", "FAILED");
                    if (loginAuth == null) {
                        redirectObj.put("Message", "找不到认证方式 '" + authType + "'");
                        logger.warn("======= login error: 找不到认证方式 '" + authType + "'");
                    } else {
                        redirectObj.put("Message", "认证失败(" + loginAuth.getType() + ")，请重新登录");
                        logger.warn("======= login error: 通过" + loginAuth.getType() + "认证失败，请重新登录");
                    }
                    redirectObj.put("DirectUrl", defaultLoginAuth.directUrl());
                } else {
                    response.setStatus(522);
                    redirectObj.put("Status", "FAILED");
                    redirectObj.put("Message", "会话已超时或已被终止，请重新登录");
                    logger.warn("======= login error: 会话已超时或已被终止，请重新登录");
                    redirectObj.put("DirectUrl", defaultLoginAuth.directUrl());
                }
                removeAuthCookie(response);
                response.setContentType(Config.RESPONSE_TYPE_JSON);
                response.getWriter().print(redirectObj.toJSONString());
            }
        } catch (Exception ex) {
            logger.error("认证失败", ex);
            response.setStatus(522);
            redirectObj.put("Status", "FAILED");
            redirectObj.put("Message", ex.getMessage());
            redirectObj.put("DirectUrl", defaultLoginAuth.directUrl());
            removeAuthCookie(response);
            response.setContentType(Config.RESPONSE_TYPE_JSON);
            response.getWriter().print(redirectObj.toJSONString());
        }
        //logger.info("requestUrl:"+request.getRequestURI()+" ------------------------------------------------ end");
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
     */
    private boolean userExpirationValid() {
        String userUuid = UserContext.get().getUserUuid();
        String tenant = TenantContext.get().getTenantUuid();
        if (UserSessionCache.getItem(tenant, userUuid) == null) {
            UserSessionVo userSessionVo = userSessionMapper.getUserSessionByUserUuid(userUuid);
            if (null != userSessionVo) {
                Date visitTime = userSessionVo.getSessionTime();
                Date now = new Date();
                int expire = Config.USER_EXPIRETIME();
                long expireTime = expire * 60L * 1000L + visitTime.getTime();
                if (now.getTime() < expireTime) {
                    userSessionMapper.updateUserSession(userUuid);
                    UserSessionCache.addItem(tenant, userUuid);
                    return true;
                }
                userSessionMapper.deleteUserSessionByUserUuid(userUuid);
            }
        } else {
            return true;
        }
        return false;
    }


}
