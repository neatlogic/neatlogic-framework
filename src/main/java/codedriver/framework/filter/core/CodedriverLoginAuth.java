package codedriver.framework.filter.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.zip.GZIPInputStream;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import codedriver.framework.common.config.Config;
import codedriver.framework.dto.UserVo;
@Service
public class CodedriverLoginAuth extends LoginAuthBase{

    @Override
    public String getType() {
        return "codedriver";
    }

    @Override
    public UserVo myAuth(HttpServletRequest request) throws ServletException, IOException{
        Cookie[] cookies = request.getCookies();
        UserVo userVo = new UserVo();
        String authorizationFromCookie = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("codedriver_authorization".equals(cookie.getName())) {
                    authorizationFromCookie = cookie.getValue();
                }
            }
        }
        
        String authorization = request.getHeader("Authorization");

        if (StringUtils.isBlank(authorization) && StringUtils.isNotBlank(authorizationFromCookie)) {
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
                    authorization = new String(bos.toByteArray());
                    bos.close();
                } catch (Exception ex) {
                    logger.error(ex.getMessage(), ex);
                }
            }
        }

        if (StringUtils.isNotBlank(authorization)) {
            if (authorization.startsWith("Bearer") && authorization.length() > 7) {
                String jwt = authorization.substring(7);
                String[] jwtParts = jwt.split("\\.");
                if (jwtParts.length == 3) {
                    SecretKeySpec signingKey = new SecretKeySpec(Config.JWT_SECRET().getBytes(), "HmacSHA1");
                    Mac mac;
                    try {
                        mac = Mac.getInstance("HmacSHA1");
                        mac.init(signingKey);
                        byte[] rawHmac = mac.doFinal((jwtParts[0] + "." + jwtParts[1]).getBytes());
                        String result = Base64.getUrlEncoder().encodeToString(rawHmac);
                        if (result.equals(jwtParts[2])) {
                            String jwtBody = new String(Base64.getUrlDecoder().decode(jwtParts[1]), "utf-8");
                            JSONObject jwtBodyObj = JSONObject.parseObject(jwtBody);
                            userVo.setUuid(jwtBodyObj.getString("useruuid"));
                            userVo.setUserId(jwtBodyObj.getString("userid"));
                            userVo.setUserName(jwtBodyObj.getString("username"));
                            userVo.setRoleUuidList(JSONArray.parseArray(jwtBodyObj.getJSONArray("rolelist").toJSONString(),String.class));
                            return userVo;
                        }
                    } catch (NoSuchAlgorithmException | InvalidKeyException e) {
                        e.printStackTrace();
                    }

                }
            }
            userVo.setAuthorization(authorization);
            userVo.setIsHasAuthorization(true);
        }
        return userVo;
    }

    @Override
    public String directUrl() {
        return "/login.html";
    }

}
