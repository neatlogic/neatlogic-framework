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

package neatlogic.framework.filter.core;

import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.asynchronization.threadlocal.UserContext;
import neatlogic.framework.common.config.Config;
import neatlogic.framework.common.constvalue.DeviceType;
import neatlogic.framework.common.util.CommonUtil;
import neatlogic.framework.dao.cache.UserSessionCache;
import neatlogic.framework.dao.mapper.LoginMapper;
import neatlogic.framework.dao.mapper.RoleMapper;
import neatlogic.framework.dao.mapper.UserMapper;
import neatlogic.framework.dao.mapper.UserSessionMapper;
import neatlogic.framework.dto.AuthenticationInfoVo;
import neatlogic.framework.dto.JwtVo;
import neatlogic.framework.dto.UserVo;
import neatlogic.framework.dto.captcha.LoginFailedCountVo;
import neatlogic.framework.service.AuthenticationInfoService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Objects;
import java.util.zip.GZIPOutputStream;

@DependsOn("loginService")
public abstract class LoginAuthHandlerBase implements ILoginAuthHandler {
    protected static Logger logger = LoggerFactory.getLogger(LoginAuthHandlerBase.class);

    public abstract String getType();

    protected static UserMapper userMapper;

    protected static RoleMapper roleMapper;

    protected static LoginMapper loginMapper;

    protected static UserSessionMapper userSessionMapper;

    protected static AuthenticationInfoService authenticationInfoService;

    @Autowired
    public void setUserMapper(UserMapper _userMapper) {
        userMapper = _userMapper;
    }

    @Autowired
    public void setRoleMapper(RoleMapper _roleMapper) {
        roleMapper = _roleMapper;
    }

    @Autowired
    public void setLoginMapper(LoginMapper _loginMapper) {
        loginMapper = _loginMapper;
    }

    @Autowired
    public void setUserSessionMapper(UserSessionMapper _userSessionMapper) {
        userSessionMapper = _userSessionMapper;
    }

    @Autowired
    public void setAuthenticationInfoService(AuthenticationInfoService _authenticationInfoService) {
        authenticationInfoService = _authenticationInfoService;
    }

    @Override
    public UserVo auth(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String tenant = request.getHeader("tenant");
        UserVo userVo = myAuth(request);
        //如果userVo没有uuid则这个user不合法，直接置null
        if (userVo != null && StringUtils.isBlank(userVo.getUuid())) {
            if (!Objects.equals(getType(), "default")) {
                logger.error(getType() + " return userVo invalid!! userVo must include uuid");
            }
            userVo = null;
        }
        //如果认证cookie为null,说明不是通过登录页登录，而是通过第三方认证接口认证。第一次认证通过后需构建并设置response 认证 cookie
        if (userVo != null && StringUtils.isBlank(userVo.getCookieAuthorization())) {
            logger.debug("======= myAuth: " + getType() + " ===== " + userVo.getUserId());
            AuthenticationInfoVo authenticationInfoVo = authenticationInfoService.getAuthenticationInfo(userVo.getUuid());
            JwtVo jwtVo = buildJwt(userVo, authenticationInfoVo);
            setResponseAuthCookie(response, request, tenant, jwtVo);
            String AuthenticationInfoStr = null;
            if (authenticationInfoVo != null) {
                if (CollectionUtils.isNotEmpty(authenticationInfoVo.getUserUuidList()) || CollectionUtils.isNotEmpty(authenticationInfoVo.getTeamUuidList()) || CollectionUtils.isNotEmpty(authenticationInfoVo.getRoleUuidList())) {
                    authenticationInfoVo.setHeaderSet(null);
                    AuthenticationInfoStr = JSONObject.toJSONString(authenticationInfoVo);
                }
            }
            if (isValidTokenCreateTime()) {
                userSessionMapper.insertUserSession(userVo.getUuid(), jwtVo.getTokenHash(), jwtVo.getTokenCreateTime(), AuthenticationInfoStr);
            } else {
                jwtVo.setValidTokenCreateTime(isValidTokenCreateTime());
                if (UserSessionCache.getItem(jwtVo.getTokenHash()) == null) {
                    userSessionMapper.insertUserSessionWithoutTokenCreateTime(userVo.getUuid(), jwtVo.getTokenHash(), jwtVo.getTokenCreateTime(), AuthenticationInfoStr);
                }
            }
            userVo.setJwtVo(jwtVo);
        }
        return userVo;
    }

    /**
     * 自定义认证，返回的用户对象，必须包含uuid,否则返回的用户无效
     */
    public abstract UserVo myAuth(HttpServletRequest request) throws Exception;

    /**
     * 生成jwt对象
     *
     * @param checkUserVo 用户
     * @return jwt对象
     * @throws Exception 异常
     */
    public static JwtVo buildJwt(UserVo checkUserVo, AuthenticationInfoVo authenticationInfoVo) throws Exception {
        Long tokenCreateTime = System.currentTimeMillis();
        JwtVo jwtVo = new JwtVo(checkUserVo, tokenCreateTime, authenticationInfoVo);
        SecretKeySpec signingKey = new SecretKeySpec(Config.JWT_SECRET().getBytes(), "HmacSHA1");
        Mac mac;
        mac = Mac.getInstance("HmacSHA1");
        mac.init(signingKey);
        byte[] rawHmac = mac.doFinal((jwtVo.getJwthead() + "." + jwtVo.getJwtbody()).getBytes());
        String jwtsign = Base64.getUrlEncoder().encodeToString(rawHmac);
        // 压缩cookie内容
        String c = "Bearer_" + jwtVo.getJwthead() + "." + jwtVo.getJwtbody() + "." + jwtsign;
        checkUserVo.setAuthorization(c);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        GZIPOutputStream gzipOutputStream = new GZIPOutputStream(bos);
        gzipOutputStream.write(c.getBytes());
        gzipOutputStream.close();
        String cc = Base64.getEncoder().encodeToString(bos.toByteArray());
        bos.close();
        jwtVo.setCc(cc);
        jwtVo.setJwtsign(jwtsign);
        checkUserVo.setJwtVo(jwtVo);
        return jwtVo;
    }

    /**
     * 生成jwt对象
     *
     * @param checkUserVo 用户
     * @return jwt对象
     * @throws Exception 异常
     */
    public static JwtVo buildJwt(UserVo checkUserVo) throws Exception {
        return buildJwt(checkUserVo, new AuthenticationInfoVo());
    }

    /**
     * 设置登录cookie
     *
     * @param response 响应
     * @param request  请求
     * @param tenant   租户
     * @param jwtVo    jwt对象
     */
    public static void setResponseAuthCookie(HttpServletResponse response, HttpServletRequest request, String tenant, JwtVo jwtVo) {
        Cookie authCookie = new Cookie("neatlogic_authorization", "GZIP_" + jwtVo.getCc());
        authCookie.setPath("/" + tenant);
        String domainName = request.getServerName();
        if (StringUtils.isNotBlank(domainName)) {
            String[] ds = domainName.split("\\.");
            int len = ds.length;
            if (len > 2 && !StringUtils.isNumeric(ds[len - 1])) {
                authCookie.setDomain(ds[len - 2] + "." + ds[len - 1]);
            }
        }
        Cookie tenantCookie = new Cookie("neatlogic_tenant", tenant);
        tenantCookie.setPath("/" + tenant);
        response.addCookie(authCookie);
        response.addCookie(tenantCookie);
        // 允许跨域携带cookie
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setContentType(Config.RESPONSE_TYPE_JSON);
    }

    @Override
    public String logout() {
        UserSessionCache.removeItem(UserContext.get().getTokenHash());
        userSessionMapper.deleteUserSessionByTokenHash(UserContext.get().getTokenHash());
        String url;
        try {
            String device = CommonUtil.getDevice();
            if (StringUtils.isNotBlank(device) && Objects.equals(DeviceType.MOBILE.getValue(), device)) {
                url = mobileLogout();
            } else {
                url = myLogout();
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
            throw new RuntimeException();
        }
        return url;
    }

    @Override
    public String mobileLogout(){
        return null;
    }

    protected String myLogout() throws IOException {
        return null;
    }

    @Override
    public String directUrl() {
        String directUrl;
        String device = CommonUtil.getDevice();
        if (StringUtils.isNotBlank(device) && Objects.equals(DeviceType.MOBILE.getValue(), device)) {
            directUrl = mobileDirectUrl();
        } else {
            directUrl = myDirectUrl();
        }
        if (StringUtils.isBlank(directUrl)) {
            directUrl = Config.DIRECT_URL();
        }
        return directUrl;
    }

    @Override
    public String mobileDirectUrl(){
        return null;
    }

    protected String myDirectUrl() {
        return null;
    }

    @Override
    public UserVo login(UserVo userVo, JSONObject resultJson) {
        UserVo checkUserVo = myLogin(userVo, resultJson);
        LoginFailedCountVo loginFailedCountVo = new LoginFailedCountVo();
        if (checkUserVo == null) {//如果正常用户登录失败则，失败次数+1
            int failedCount = 1;
            loginFailedCountVo = loginMapper.getLoginFailedCountVoByUserId(userVo.getUserId());
            if (loginFailedCountVo != null) {
                failedCount = loginFailedCountVo.getFailedCount();
            }
            loginFailedCountVo = new LoginFailedCountVo(userVo.getUserId(), failedCount);
            loginMapper.updateLoginFailedCount(loginFailedCountVo);
        } else {//如果正常用户登录成功，则清空该用户的失败次数
            resultJson.remove("isNeedCaptcha");
            loginMapper.deleteLoginFailedCountByUserId(userVo.getUserId());
        }
        return checkUserVo;
    }

    public UserVo myLogin(UserVo userVo, JSONObject resultJson) {
        return userMapper.getUserByUserIdAndPassword(userVo);
    }

    @Override
    public boolean isValidTokenCreateTime() {
        return true;
    }
}
