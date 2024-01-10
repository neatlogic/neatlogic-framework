/*
Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package neatlogic.module.framework.filter.handler;

import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.common.config.Config;
import neatlogic.framework.dto.JwtVo;
import neatlogic.framework.dto.UserVo;
import neatlogic.framework.filter.core.LoginAuthHandlerBase;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.zip.GZIPInputStream;
@Service
public class DefaultLoginAuthHandler extends LoginAuthHandlerBase {

    @Override
    public String getType() {
        return "default";
    }

    @Override
    public UserVo myAuth(HttpServletRequest request) throws ServletException, IOException{
        //获取 authorization，优先获取header的authorization，不存在则从cookie获取authorization
        Cookie[] cookies = request.getCookies();
        UserVo userVo = new UserVo();
        String authorizationFromCookie = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("neatlogic_authorization".equals(cookie.getName())) {
                    authorizationFromCookie = cookie.getValue();
                }
            }
        }
        
        String authorization = request.getHeader("Authorization");

        if (StringUtils.isBlank(authorization) ) {
            if (StringUtils.isNotBlank(authorizationFromCookie)) {
                userVo.setCookieAuthorization(authorizationFromCookie);
                authorization = authorizationFromCookie;
                // 解压cookie内容
                if (authorization.startsWith("GZIP_")) {
                    authorization = authorization.substring(5);
                    try {
                        byte[] compressDatas = Base64.getDecoder().decode(authorization);
                        ByteArrayInputStream bis = new ByteArrayInputStream(compressDatas);
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        GZIPInputStream gzipInputStream = new GZIPInputStream(bis);
                        byte[] buffer = new byte[2048];
                        int n;
                        while ((n = gzipInputStream.read(buffer)) >= 0) {
                            bos.write(buffer, 0, n);
                        }
                        bis.close();
                        gzipInputStream.close();
                        authorization = bos.toString();
                        bos.close();
                    } catch (Exception ex) {
                        logger.error(ex.getMessage(), ex);
                    }
                }
            }
        }else{
            userVo.setAuthorization(authorization);
        }
        //如果 authorization 存在，则解包获取用户信息
        if (StringUtils.isNotBlank(authorization)) {
            if (authorization.startsWith("Bearer") && authorization.length() > 7) {
                String jwt = authorization.substring(7);
                String[] jwtParts = jwt.split("\\.");
                if (jwtParts.length == 3) {
                    userVo.setJwtVo(new JwtVo(jwtParts));
                    SecretKeySpec signingKey = new SecretKeySpec(Config.JWT_SECRET().getBytes(), "HmacSHA1");
                    Mac mac;
                    try {
                        mac = Mac.getInstance("HmacSHA1");
                        mac.init(signingKey);
                        byte[] rawHmac = mac.doFinal((jwtParts[0] + "." + jwtParts[1]).getBytes());
                        String result = Base64.getUrlEncoder().encodeToString(rawHmac);
                        if (result.equals(jwtParts[2])) {
                            String jwtBody = new String(Base64.getUrlDecoder().decode(jwtParts[1]), StandardCharsets.UTF_8);
                            JSONObject jwtBodyObj = JSONObject.parseObject(jwtBody);
                            userVo.setUuid(jwtBodyObj.getString("useruuid"));
                            userVo.setUserId(jwtBodyObj.getString("userid"));
                            userVo.setUserName(jwtBodyObj.getString("username"));
                            userVo.setIsSuperAdmin(jwtBodyObj.getBoolean("isSuperAdmin"));
                            userVo.getJwtVo().setTokenCreateTime(jwtBodyObj.getLong("createTime"));
                            return userVo;
                        }
                    } catch (NoSuchAlgorithmException | InvalidKeyException e) {
                        e.printStackTrace();
                    }

                }
            }

        }
        return userVo;
    }

    @Override
    public String myDirectUrl() {
        return Config.DIRECT_URL();
    }

}
