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

package neatlogic.framework.filter.core;

import neatlogic.framework.asynchronization.threadlocal.UserContext;
import neatlogic.framework.common.config.Config;
import neatlogic.framework.dao.mapper.RoleMapper;
import neatlogic.framework.dao.mapper.UserMapper;
import neatlogic.framework.dao.mapper.UserSessionMapper;
import neatlogic.framework.dto.JwtVo;
import neatlogic.framework.dto.UserVo;
import com.alibaba.fastjson.JSONObject;
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
import java.util.Base64;
import java.util.zip.GZIPOutputStream;

@DependsOn("loginService")
public abstract class LoginAuthHandlerBase implements ILoginAuthHandler {
    protected static Logger logger = LoggerFactory.getLogger(LoginAuthHandlerBase.class);

    public abstract String getType();

    protected static UserMapper userMapper;

    protected static RoleMapper roleMapper;

    protected static UserSessionMapper userSessionMapper;

    @Autowired
    public void setUserMapper(UserMapper _userMapper) {
        userMapper = _userMapper;
    }

    @Autowired
    public void setRoleMapper(RoleMapper _roleMapper) {
        roleMapper = _roleMapper;
    }

    @Autowired
    public void setUserSessionMapper(UserSessionMapper _userSessionMapper) {
        userSessionMapper = _userSessionMapper;
    }

    @Override
    public UserVo auth(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String type = this.getType();
        String tenant = request.getHeader("tenant");
        UserVo userVo = myAuth(request);
        //logger.info("loginAuth type: " + type);
        if (userVo != null) {
            //logger.info("get userUuId: " + userVo.getUuid());
            //logger.info("get userId: " + userVo.getUserId());
        }
        //??????????????? cookie authorization????????????jwt,??????????????????????????? default ??????
        if (userVo != null && StringUtils.isNotBlank(userVo.getUuid()) && StringUtils.isBlank(userVo.getCookieAuthorization())) {
            JwtVo jwtVo = buildJwt(userVo);
            setResponseAuthCookie(response, request, tenant, jwtVo);
            userVo.setRoleUuidList(roleMapper.getRoleUuidListByUserUuid(userVo.getUuid()));
            userSessionMapper.insertUserSession(userVo.getUuid());
        }
        return userVo;
    }

    public abstract UserVo myAuth(HttpServletRequest request) throws Exception;

    /**
     * ??????jwt??????
     *
     * @param checkUserVo ??????
     * @return jwt??????
     * @throws Exception ??????
     */
    public static JwtVo buildJwt(UserVo checkUserVo) throws Exception {
        JSONObject jwtHeadObj = new JSONObject();
        jwtHeadObj.put("alg", "HS256");
        jwtHeadObj.put("typ", "JWT");

        JSONObject jwtBodyObj = new JSONObject();
        jwtBodyObj.put("useruuid", checkUserVo.getUuid());
        jwtBodyObj.put("userid", checkUserVo.getUserId());
        jwtBodyObj.put("username", checkUserVo.getUserName());
        jwtBodyObj.put("tenant", checkUserVo.getTenant());
        jwtBodyObj.put("isSuperAdmin", checkUserVo.getIsSuperAdmin());
        if (CollectionUtils.isNotEmpty(checkUserVo.getRoleUuidList())) {
            jwtBodyObj.put("rolelist", checkUserVo.getRoleUuidList());
        }

        String jwthead = Base64.getUrlEncoder().encodeToString(jwtHeadObj.toJSONString().getBytes());
        String jwtbody = Base64.getUrlEncoder().encodeToString(jwtBodyObj.toJSONString().getBytes());

        SecretKeySpec signingKey = new SecretKeySpec(Config.JWT_SECRET().getBytes(), "HmacSHA1");
        Mac mac;

        mac = Mac.getInstance("HmacSHA1");
        mac.init(signingKey);
        byte[] rawHmac = mac.doFinal((jwthead + "." + jwtbody).getBytes());
        String jwtsign = Base64.getUrlEncoder().encodeToString(rawHmac);

        // ??????cookie??????
        String c = "Bearer_" + jwthead + "." + jwtbody + "." + jwtsign;
        checkUserVo.setAuthorization(c);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        GZIPOutputStream gzipOutputStream = new GZIPOutputStream(bos);
        gzipOutputStream.write(c.getBytes());
        gzipOutputStream.close();
        String cc = Base64.getEncoder().encodeToString(bos.toByteArray());
        bos.close();
        JwtVo jwtVo = new JwtVo();
        jwtVo.setCc(cc);
        jwtVo.setJwthead(jwthead);
        jwtVo.setJwtbody(jwtbody);
        jwtVo.setJwtsign(jwtsign);
        return jwtVo;
    }

    /**
     * ????????????cookie
     *
     * @param response ??????
     * @param request  ??????
     * @param tenant   ??????
     * @param jwtVo    jwt??????
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
        // ??????????????????cookie
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setContentType(Config.RESPONSE_TYPE_JSON);
    }

    @Override
    public void logout() {
        userSessionMapper.deleteUserSessionByUserUuid(UserContext.get().getUserUuid(true));
        myLogout();
    }

    protected void myLogout() {
    }

}
