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

package neatlogic.module.framework.filter.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.asynchronization.threadlocal.RequestContext;
import neatlogic.framework.common.config.Config;
import neatlogic.framework.config.ConfigManager;
import neatlogic.framework.config.FrameworkTenantConfig;
import neatlogic.framework.dto.JwtVo;
import neatlogic.framework.dto.UserPasswordVo;
import neatlogic.framework.dto.UserVo;
import neatlogic.framework.exception.tenant.TenantInvalidException;
import neatlogic.framework.exception.user.UserPasswordExpiredException;
import neatlogic.framework.filter.core.LoginAuthHandlerBase;
import neatlogic.framework.restful.core.privateapi.PrivateApiComponentFactory;
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
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Objects;
import java.util.zip.GZIPInputStream;

@Service
public class DefaultLoginAuthHandler extends LoginAuthHandlerBase {

    @Override
    public String getType() {
        return "default";
    }

    @Override
    public boolean isNeedAuth() {
        return false;
    }

    @Override
    public UserVo myAuth(HttpServletRequest request) throws ServletException, IOException {
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


        if (StringUtils.isBlank(authorization)) {
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
        } else {
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
                            JSONObject jwtBodyObj = JSON.parseObject(jwtBody);
                            //防止header中的租户和token不一致
                            if (!Objects.equals(request.getHeader("tenant"), jwtBodyObj.getString("tenant"))) {
                                throw new TenantInvalidException(request.getHeader("tenant"));
                            }
                            //用户密码是否过期
                            String needPwdExpiredCheck = ConfigManager.getConfig(FrameworkTenantConfig.PASSWORD_NEED_EXPIRED_CHECK);
                            if (Objects.equals(needPwdExpiredCheck, "1")
                                    && Boolean.TRUE.equals(jwtBodyObj.getBoolean("pwdExpired"))
                                    && PrivateApiComponentFactory.ExemptTokenMap.stream().noneMatch(o -> Objects.equals("/neatlogic/api/rest" + (o.startsWith("/")?"":"/") + o, RequestContext.get().getRequest().getRequestURI()))
                            ) {
                                throw new UserPasswordExpiredException();
                            }
                            userVo.setUuid(jwtBodyObj.getString("useruuid"));
                            userVo.setUserId(jwtBodyObj.getString("userid"));
                            userVo.setUserName(jwtBodyObj.getString("username"));
                            userVo.setIsSuperAdmin(jwtBodyObj.getBoolean("isSuperAdmin"));
                            userVo.getJwtVo().setTokenCreateTime(jwtBodyObj.getLong("createTime"));
                            userVo.setTenant(jwtBodyObj.getString("tenant"));
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

    @Override
    public boolean checkPwdExpired(UserVo checkUserVo) {
        UserPasswordVo userPasswordVo = userMapper.getActivePasswordByUserUuid(checkUserVo.getUuid());
        if (userPasswordVo == null || userPasswordVo.getCreateTime() == null) {
            return true;
        }
        LocalDateTime pwdCreateTime = userPasswordVo.getCreateTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        long expireDays = Long.parseLong(ConfigManager.getConfig(FrameworkTenantConfig.PASSWORD_EXPIRE_DAYS));
        LocalDateTime daysAgo = LocalDateTime.now().minusDays(expireDays);
        return pwdCreateTime.isBefore(daysAgo);
    }

}
