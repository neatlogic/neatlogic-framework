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

package neatlogic.framework.filter.core;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.asynchronization.threadlocal.RequestContext;
import neatlogic.framework.asynchronization.threadlocal.UserContext;
import neatlogic.framework.common.config.Config;
import neatlogic.framework.common.constvalue.DeviceType;
import neatlogic.framework.common.constvalue.systemuser.SystemUserFactory;
import neatlogic.framework.common.util.CommonUtil;
import neatlogic.framework.dao.mapper.*;
import neatlogic.framework.dto.*;
import neatlogic.framework.dto.loginaudit.LoginAuditVo;
import neatlogic.framework.login.core.ILoginPostProcessor;
import neatlogic.framework.login.core.LoginPostProcessorFactory;
import neatlogic.framework.service.AuthenticationInfoService;
import neatlogic.framework.service.LoginService;
import neatlogic.framework.util.HeaderUtil;
import neatlogic.framework.util.Md5Util;
import neatlogic.framework.util.SnowflakeUtil;
import neatlogic.framework.util.TimeUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.zip.GZIPOutputStream;

public abstract class LoginAuthHandlerBase implements ILoginAuthHandler {
    protected static Logger logger = LoggerFactory.getLogger(LoginAuthHandlerBase.class);


    protected static UserMapper userMapper;

    protected static RoleMapper roleMapper;

    protected static LoginMapper loginMapper;

    protected static UserSessionMapper userSessionMapper;

    protected static UserSessionContentMapper userSessionContentMapper;

    protected static AuthenticationInfoService authenticationInfoService;

    protected static LoginService loginService;

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
    public void setUserSessionContentMapper(UserSessionContentMapper _userSessionContentMapper) {
        userSessionContentMapper = _userSessionContentMapper;
    }

    @Autowired
    public void setAuthenticationInfoService(AuthenticationInfoService _authenticationInfoService) {
        authenticationInfoService = _authenticationInfoService;
    }

    @Autowired
    public void setLoginService(LoginService _loginService) {
        loginService = _loginService;
    }

    @Override
    public UserVo auth(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String tenant = request.getHeader("tenant");
        UserVo userVo = myAuth(request);
        //如果userVo没有uuid则这个user不合法，直接置null
        if (userVo != null && StringUtils.isBlank(userVo.getUuid())) {
            if (!Objects.equals(getType(), "default")) {
                logger.debug("{} return userVo invalid!! userVo must include uuid", getType());
            }
            userVo = null;
        }
        //通过第三方认证or default的header认证。第一次认证通过后需构建并设置response 认证 cookie
        if (userVo != null && (!Objects.equals(getType(), "default") || (Objects.equals(getType(), "default") && StringUtils.isBlank(userVo.getCookieAuthorization())))) {
            logger.debug("======= myAuth: {} ===== {}", getType(), userVo.getUserId());
            JwtVo jwtVo = new JwtVo();
            AuthenticationInfoVo authenticationInfoVo;
            jwtVo.setToken(getToken(userVo));
            UserSessionVo userSessionVo = userSessionMapper.getUserSessionByTokenHash(jwtVo.getTokenHash());
            boolean isNeedLoginPost = false;
            if (userSessionVo == null) {
                logger.debug("======= tokenHash: {}", jwtVo.getTokenHash());
                String authInfoHash = null;
                String authenticationInfoStr = null;
                authenticationInfoVo = authenticationInfoService.getAuthenticationInfo(userVo.getUuid());
                jwtVo = buildJwt(userVo, authenticationInfoVo, getType());
                if (isNeedCookie()) {
                    setResponseAuthCookie(response, request, tenant, jwtVo);
                }
                if (authenticationInfoVo != null && (CollectionUtils.isNotEmpty(authenticationInfoVo.getUserUuidList()) || CollectionUtils.isNotEmpty(authenticationInfoVo.getTeamUuidList()) || CollectionUtils.isNotEmpty(authenticationInfoVo.getRoleUuidList()))) {
                    authenticationInfoStr = JSON.toJSONString(authenticationInfoVo);
                    if (StringUtils.isNotBlank(authenticationInfoStr)) {
                        authInfoHash = Md5Util.encryptMD5(authenticationInfoStr);
                    }
                }
                userSessionVo = new UserSessionVo(userVo.getUuid(), jwtVo.getToken(), jwtVo.getTokenHash(), jwtVo.getTokenCreateTime(), authInfoHash, authenticationInfoStr);
                userSessionMapper.insertUserSession(userSessionVo.getUserUuid(), userSessionVo.getTokenHash(), userSessionVo.getTokenCreateTime(), userSessionVo.getAuthInfoHash());
                userSessionContentMapper.insertUserSessionContent(new UserSessionContentVo(userSessionVo.getTokenHash(), userSessionVo.getToken()));
                if (StringUtils.isNotBlank(userSessionVo.getAuthInfoHash())) {
                    userSessionContentMapper.insertUserSessionContent(new UserSessionContentVo(userSessionVo.getAuthInfoHash(), userSessionVo.getAuthInfoStr()));
                }
                isNeedLoginPost = true;
                if (SystemUserFactory.getUserVoByUser(userVo.getUuid()) == null) {
                    LoginAuditVo loginAuditVo = new LoginAuditVo();
                    loginAuditVo.setId(SnowflakeUtil.uniqueLong());
                    loginAuditVo.setUserUuid(userVo.getUuid());
                    loginAuditVo.setIp(RequestContext.get().getRemoteAddr());
                    loginAuditVo.setLoginMethod(this.getType());
                    loginMapper.insertLoginAudit(loginAuditVo);
                }
            } else {
                String autoInfoStr = userSessionContentMapper.getUserSessionContentByHash(userSessionVo.getAuthInfoHash());
                if (StringUtils.isNotBlank(autoInfoStr)) {
                    authenticationInfoVo = JSON.toJavaObject(JSON.parseObject(autoInfoStr), AuthenticationInfoVo.class);
                } else {
                    //系统用户或者没有分组和角色的用户
                    authenticationInfoVo = new AuthenticationInfoVo();
                }
                //如果没有cookie则补充cookie。因为UserSessionCache，兼容移动端认证浏览器cookie可能存在丢失重新认证却拿不到cookie的问题
                if (isNeedCookie() && StringUtils.isBlank(userVo.getCookieAuthorization())) {
                    jwtVo = buildJwt(userVo, authenticationInfoVo, getType());
                    setResponseAuthCookie(response, request, tenant, jwtVo);
                }
            }
            userVo.setJwtVo(jwtVo);
            assert authenticationInfoVo != null;
            authenticationInfoVo.setUserUuid(userVo.getUuid());
            UserContext.init(userVo, authenticationInfoVo, TimeUtil.ZONE_TIME);
            if (isNeedLoginPost) {
                for (ILoginPostProcessor loginPostProcessor : LoginPostProcessorFactory.getLoginPostProcessorSet()) {
                    loginPostProcessor.loginAfterInitialization();
                }
            }
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
    public static JwtVo buildJwt(UserVo checkUserVo, AuthenticationInfoVo authenticationInfoVo, String authType) throws Exception {
        Long tokenCreateTime = System.currentTimeMillis();
        JwtVo jwtVo = new JwtVo(checkUserVo, tokenCreateTime, authenticationInfoVo, authType);
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
        return jwtVo;
    }

    public static String getToken(UserVo checkUserVo) {
        Long tokenCreateTime = System.currentTimeMillis();
        //补充满足前缀的header
        Set<String> headerSet = new HashSet<>();
        HeaderUtil.getRulePrefixHeader(headerSet);
        AuthenticationInfoVo authenticationInfoVo = new AuthenticationInfoVo(headerSet);
        JwtVo jwtVo = new JwtVo(checkUserVo, tokenCreateTime, authenticationInfoVo);
        return jwtVo.getToken();
    }

    /**
     * 生成jwt对象
     *
     * @param checkUserVo 用户
     * @return jwt对象
     * @throws Exception 异常
     */
    public static JwtVo buildJwt(UserVo checkUserVo) throws Exception {
        return buildJwt(checkUserVo, new AuthenticationInfoVo(), null);
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
        //仅删除自己创建的session
        JwtVo jwtVo = UserContext.get().getJwtVo();
        if (jwtVo != null) {
            UserSessionVo userSessionVo = userSessionMapper.getUserSessionByTokenHashWithoutCache(jwtVo.getTokenHash());
            if (userSessionVo != null && Objects.equals(userSessionVo.getTokenCreateTime(), jwtVo.getTokenCreateTime())) {
                userSessionMapper.deleteUserSessionByTokenHash(UserContext.get().getTokenHash());
            }
        }
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
    public String mobileLogout() {
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
    public String mobileDirectUrl() {
        return null;
    }

    protected String myDirectUrl() {
        return null;
    }

    @Override
    public UserVo login(UserVo userVo, JSONObject resultJson) {
        UserVo checkUserVo = myLogin(userVo, resultJson);
        loginService.updateFailCount(userVo, resultJson, checkUserVo);
        if (checkUserVo != null && SystemUserFactory.getUserVoByUser(userVo.getUuid()) == null) {
            LoginAuditVo loginAuditVo = new LoginAuditVo();
            loginAuditVo.setId(SnowflakeUtil.uniqueLong());
            loginAuditVo.setUserUuid(checkUserVo.getUuid());
            loginAuditVo.setIp(RequestContext.get().getRemoteAddr());
            loginAuditVo.setLoginMethod(getType());
            loginMapper.insertLoginAudit(loginAuditVo);
        }
        return checkUserVo;
    }

    public UserVo myLogin(UserVo userVo, JSONObject resultJson) {
        return userMapper.getUserByUserIdAndPassword(userVo);
    }

    @Override
    public String pwdExpiredDirectUrl() {
        String directUrl;
        String device = CommonUtil.getDevice();
        if (StringUtils.isNotBlank(device) && Objects.equals(DeviceType.MOBILE.getValue(), device)) {
            directUrl = mobilePwdExpiredDirectUrl();
        } else {
            directUrl = myPwdExpiredDirectUrl();
        }
        return directUrl;
    }

    String mobilePwdExpiredDirectUrl() {
        return null;
    }

    String myPwdExpiredDirectUrl() {
        return null;
    }
}
