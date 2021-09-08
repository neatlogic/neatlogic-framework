/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.filter;

import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.asynchronization.threadlocal.UserContext;
import codedriver.framework.common.config.Config;
import codedriver.framework.common.util.TenantUtil;
import codedriver.framework.dao.cache.UserSessionCache;
import codedriver.framework.dao.mapper.UserMapper;
import codedriver.framework.dto.UserSessionVo;
import codedriver.framework.dto.UserVo;
import codedriver.framework.filter.core.ILoginAuthHandler;
import codedriver.framework.filter.core.LoginAuthFactory;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.filter.OncePerRequestFilter;

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

    @Autowired
    private UserMapper userMapper;

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
        ILoginAuthHandler loginAuth = null;
        String authType = null;
        //获取时区
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("codedriver_timezone".equals(cookie.getName())) {
                    timezone = (URLDecoder.decode(cookie.getValue(), "UTF-8"));
                }
            }
        }

        //判断租户
        try {
            String tenant = request.getHeader("Tenant");
            if (TenantUtil.hasTenant(tenant)) {
                hasTenant = true;
                //先按 default 认证，不存在才根据具体 AuthType 认证用户
                loginAuth = LoginAuthFactory.getLoginAuth("default");
                userVo = loginAuth.auth(request, response);
                if (userVo == null || StringUtils.isBlank(userVo.getUuid())) {
                    authType = request.getHeader("AuthType");
                    if (StringUtils.isNotBlank(authType)) {
                        loginAuth = LoginAuthFactory.getLoginAuth(authType);
                        if (loginAuth != null) {
                            userVo = loginAuth.auth(request, response);
                        }
                    } else {
                        loginAuth = null;
                    }
                }
                if (userVo != null && StringUtils.isNotBlank(userVo.getUuid())) {
                    UserContext.init(userVo, timezone, request, response);
                    TenantContext.init();
                    TenantContext.get().switchTenant(tenant);
                    isUnExpired = userExpirationValid();
                    isAuth = true;
                }
            }

            if (hasTenant && isAuth && isUnExpired) {
                filterChain.doFilter(request, response);
            } else {
                if (!hasTenant) {
                    response.setStatus(521);
                    redirectObj.put("Status", "FAILED");
                    redirectObj.put("Message", "租户 '" + tenant + "' 不存在或已被禁用");
                } else if (loginAuth == null) {
                    response.setStatus(522);
                    redirectObj.put("Status", "FAILED");
                    redirectObj.put("Message", "找不到认证方式 '" + authType + "'");
                } else if (userVo != null && StringUtils.isBlank(userVo.getAuthorization())) {
                    response.setStatus(522);
                    redirectObj.put("Status", "FAILED");
                    redirectObj.put("Message", "没有找到认证信息，请登录");
                    redirectObj.put("directUrl", loginAuth.directUrl());
                } else if (isAuth) {
                    response.setStatus(522);
                    redirectObj.put("Status", "FAILED");
                    redirectObj.put("Message", "会话已超时或已被终止，请重新登录");
                    redirectObj.put("directUrl", loginAuth.directUrl());
                } else {
                    response.setStatus(522);
                    redirectObj.put("Status", "FAILED");
                    redirectObj.put("Message", "用户认证失败，请登录");
                    redirectObj.put("directUrl", loginAuth.directUrl());
                }
                response.setContentType(Config.RESPONSE_TYPE_JSON);
                response.getWriter().print(redirectObj.toJSONString());
            }
        } catch (Exception ex) {
            logger.error("认证失败", ex);
            response.setStatus(522);
            redirectObj.put("Status", "FAILED");
            redirectObj.put("Message", "认证失败，具体异常请查看日志");
            if (loginAuth != null) {
                redirectObj.put("directUrl", loginAuth.directUrl());
            }
            response.setContentType(Config.RESPONSE_TYPE_JSON);
            response.getWriter().print(redirectObj.toJSONString());
        }

    }

    private boolean userExpirationValid() {
        String userUuid = UserContext.get().getUserUuid();
        String tenant = TenantContext.get().getTenantUuid();
        if (UserSessionCache.getItem(tenant, userUuid) == null) {
            UserSessionVo userSessionVo = userMapper.getUserSessionLockByUserUuid(userUuid);
            if (null != userSessionVo) {
                Date visitTime = userSessionVo.getSessionTime();
                Date now = new Date();
                int expire = Config.USER_EXPIRETIME();
                long expireTime = expire * 60L * 1000L + visitTime.getTime();
                if (now.getTime() < expireTime) {
                    userMapper.updateUserSession(userUuid);
                    UserSessionCache.addItem(tenant, userUuid);
                    return true;
                }
                userMapper.deleteUserSessionByUserUuid(userUuid);
            }
        } else {
            return true;
        }
        return false;
    }


}
