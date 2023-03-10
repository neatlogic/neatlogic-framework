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
        boolean isAuth = false;
        boolean isUnExpired = false;
        boolean hasTenant = false;
        UserVo userVo = null;
        JSONObject redirectObj = new JSONObject();
        ILoginAuthHandler defaultLoginAuth = LoginAuthFactory.getLoginAuth("default");
        ILoginAuthHandler loginAuth = defaultLoginAuth;
        String authType = null;
        //????????????
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("neatlogic_timezone".equals(cookie.getName())) {
                    timezone = (URLDecoder.decode(cookie.getValue(), "UTF-8"));
                }
            }
        }
        //????????????
        try {
            //logger.info("requestUrl:"+request.getRequestURI()+" ------------------------------------------------ start");
            String tenant = request.getHeader("Tenant");
            //??????????????????????????????request?????????inputStream???????????????spring???????????????inputStream??????????????????cached
            HttpServletRequest cachedRequest = new CachedBodyHttpServletRequest(request);
            if (TenantUtil.hasTenant(tenant)) {
                hasTenant = true;
                TenantContext.init();
                TenantContext.get().switchTenant(tenant);
                //?????? default ????????????????????????????????? AuthType ????????????
                userVo = defaultLoginAuth.auth(cachedRequest, response);
                AuthenticationInfoVo authenticationInfoVo = null;
                if (userVo == null || StringUtils.isBlank(userVo.getUuid())) {
                    authType = request.getHeader("AuthType");
                    logger.info("AuthType: " + authType);
                    if (StringUtils.isNotBlank(authType)) {
                        loginAuth = LoginAuthFactory.getLoginAuth(authType);
                        if (loginAuth != null) {
                            userVo = loginAuth.auth(cachedRequest, response);
                            if (userVo != null && StringUtils.isNotBlank(userVo.getUuid())) {
                                authenticationInfoVo = authenticationInfoService.getAuthenticationInfo(userVo.getUuid());
                                UserContext.init(userVo, authenticationInfoVo, timezone, request, response);
                                for (ILoginPostProcessor loginPostProcessor : LoginPostProcessorFactory.getLoginPostProcessorSet()) {
                                    loginPostProcessor.loginAfterInitialization();
                                }
                            }
                        }
                    }
                }
                if (userVo != null && StringUtils.isNotBlank(userVo.getUuid())) {
                    if (authenticationInfoVo == null) {
                        authenticationInfoVo = authenticationInfoService.getAuthenticationInfo(userVo.getUuid());
                        UserContext.init(userVo, authenticationInfoVo, timezone, request, response);
                    }
                    isUnExpired = userExpirationValid();
                    isAuth = true;
                }
            }

            if (hasTenant && isAuth && isUnExpired) {
                filterChain.doFilter(cachedRequest, response);
            } else {
                if (!hasTenant) {
                    response.setStatus(521);
                    redirectObj.put("Status", "FAILED");
                    redirectObj.put("Message", "?????? '" + tenant + "' ????????????????????????");
                } else if (loginAuth == null) {
                    response.setStatus(522);
                    redirectObj.put("Status", "FAILED");
                    redirectObj.put("Message", "????????????????????? '" + authType + "'");
                    redirectObj.put("DirectUrl", defaultLoginAuth.directUrl());
                } else if (userVo != null && StringUtils.isBlank(userVo.getAuthorization())) {
                    response.setStatus(522);
                    redirectObj.put("Status", "FAILED");
                    redirectObj.put("Message", "????????????????????????????????????");
                    redirectObj.put("DirectUrl", defaultLoginAuth.directUrl());
                } else if (isAuth) {
                    response.setStatus(522);
                    redirectObj.put("Status", "FAILED");
                    redirectObj.put("Message", "????????????????????????????????????????????????");
                    redirectObj.put("DirectUrl", defaultLoginAuth.directUrl());
                } else {
                    response.setStatus(522);
                    redirectObj.put("Status", "FAILED");
                    redirectObj.put("Message", "??????????????????????????????");
                    redirectObj.put("DirectUrl", defaultLoginAuth.directUrl());
                }
                response.setContentType(Config.RESPONSE_TYPE_JSON);
                response.getWriter().print(redirectObj.toJSONString());
            }
        } catch (Exception ex) {
            logger.error("????????????", ex);
            response.setStatus(522);
            redirectObj.put("Status", "FAILED");
            redirectObj.put("Message", ex.getMessage());
            redirectObj.put("DirectUrl", defaultLoginAuth.directUrl());
            response.setContentType(Config.RESPONSE_TYPE_JSON);
            response.getWriter().print(redirectObj.toJSONString());
        }
        //logger.info("requestUrl:"+request.getRequestURI()+" ------------------------------------------------ end");
    }

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
