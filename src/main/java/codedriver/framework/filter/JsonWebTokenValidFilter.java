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
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.Date;

/**
 * @Author:chenqiwei
 * @Time:Aug 25, 2020
 * @ClassName: JsonWebTokenValidFilter
 */
public class JsonWebTokenValidFilter extends OncePerRequestFilter {
    // private ServletContext context;

    @Autowired
    private UserMapper userMapper;

    /**
     * Default constructor.
     */
    public JsonWebTokenValidFilter() {
        // TODO Auto-generated constructor stub
    }

    /**
     * @see Filter#destroy()
     */
    @Override
    public void destroy() {
        TenantContext.get().release();// 清除线程变量值
        UserContext.get().release();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        Cookie[] cookies = request.getCookies();
        String timezone = "+8:00";
        boolean isAuth = false;
        boolean isUnExpired = false;
        boolean hasTenant = false;
        UserVo userVo = null;
        JSONObject redirectObj = null;
        ILoginAuthHandler loginAuth = null;
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
            String tenant =  request.getHeader("Tenant");
            if (TenantUtil.hasTenant(tenant)) {
                hasTenant = true;
                String auth = request.getHeader("AuthType");
                if(StringUtils.isBlank(auth)){
                    auth = "default";
                }
                loginAuth = LoginAuthFactory.getLoginAuth(auth);
                userVo = loginAuth.auth(request,response);

                if(userVo != null ) {
                    if(StringUtils.isNotBlank(userVo.getUuid())){
                        JSONObject jwtBodyObj = new JSONObject();
                        jwtBodyObj.put("useruuid", userVo.getUuid());
                        jwtBodyObj.put("userid", userVo.getUserId());
                        jwtBodyObj.put("username", userVo.getUserName());
                        jwtBodyObj.put("tenant", tenant);
                        if (CollectionUtils.isNotEmpty(userVo.getRoleUuidList())) {
                            JSONArray roleList = new JSONArray();
                            roleList.addAll(userVo.getRoleUuidList());
                            jwtBodyObj.put("rolelist", roleList);
                        }
                        UserContext.init(jwtBodyObj, userVo.getAuthorization(), timezone, request, response);
                        TenantContext.init();
                        TenantContext.get().switchTenant(tenant);
                        isUnExpired = userExpirationValid();
                        isAuth = true;
                    }
                }
            }
            
            if (hasTenant && isAuth && isUnExpired) {
                filterChain.doFilter(request, response);
            } else {
                redirectObj = new JSONObject();
                 if (!hasTenant) {
                    response.setStatus(521);
                    redirectObj.put("Status", "FAILED");
                    redirectObj.put("Message", "租户" + tenant + "不存在或已被禁用");
                } else if (!userVo.getIsHasAuthorization()) {
                    response.setStatus(522);
                    redirectObj.put("Status", "FAILED");
                    redirectObj.put("Message", "没有找到认证信息，请登录");
                    redirectObj.put("directUrl", loginAuth.directUrl());
                } else if (!isAuth) {
                    response.setStatus(522);
                    redirectObj.put("Status", "FAILED");
                    redirectObj.put("Message", "没有找到登录信息，请登录");
                    redirectObj.put("directUrl", loginAuth.directUrl());
                } else if (!isUnExpired) {
                    response.setStatus(522);
                    redirectObj.put("Status", "FAILED");
                    redirectObj.put("Message", "会话已超时或已被终止，请重新登录");
                    redirectObj.put("directUrl", loginAuth.directUrl());
                }
                response.setContentType(Config.RESPONSE_TYPE_JSON);
                response.getWriter().print(redirectObj.toJSONString());
            }
        }catch(Exception  ex) {
            logger.error("认证失败", ex);
            response.setStatus(522);
            redirectObj.put("Status", "FAILED");
            redirectObj.put("Message", "认证失败，具体异常请查看日志");
            redirectObj.put("directUrl", loginAuth.directUrl());
            response.setContentType(Config.RESPONSE_TYPE_JSON);
            response.getWriter().print(redirectObj.toJSONString());
        }
        // 清除所有threadlocal
        if (TenantContext.get() != null) {
            TenantContext.get().release();
        }
        if (UserContext.get() != null) {
            UserContext.get().release();
        }
    }

    private boolean userExpirationValid() {
        String userUuid = UserContext.get().getUserUuid();
        String tenant = TenantContext.get().getTenantUuid();
        if (UserSessionCache.getItem(tenant, userUuid) == null) {
            UserSessionVo userSessionVo = userMapper.getUserSessionByUserUuid(userUuid);
            if (null != userSessionVo) {
                Date visitTime = userSessionVo.getSessionTime();
                Date now = new Date();
                int expire = Config.USER_EXPIRETIME();
                Long expireTime = expire * 60 * 1000 + visitTime.getTime();
                if (now.getTime() < expireTime) {
                    userMapper.updateUserSession(userUuid);
                    UserSessionCache.addItem(tenant, userUuid);
                    return true;
                }
            }
        } else {
            return true;
        }
        return false;
    }
    

}
