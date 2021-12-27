/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.module.framework.filter.handler;

import codedriver.framework.dao.mapper.UserMapper;
import codedriver.framework.dto.UserVo;
import codedriver.framework.exception.hmac.HeaderIrregularException;
import codedriver.framework.exception.hmac.HeaderNotFoundException;
import codedriver.framework.exception.user.UserNotFoundException;
import codedriver.framework.exception.user.UserTokenNotFoundException;
import codedriver.framework.filter.core.LoginAuthHandlerBase;
import codedriver.framework.util.SHA256Util;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.util.Base64;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Service
public class HmacLoginAuthHandler extends LoginAuthHandlerBase {

    @Resource
    private UserMapper userMapper;

    @Override
    public String getType() {
        return "hmac";
    }

    /*
    请求超时时间，默认5分钟
     */
    //private final int expiretime = 60;

    /**
     * 头部说明：
     * x-access-key="用户名"
     * Authorization="Hmac 签名后的sha256散列值"
     */
    @Override
    public UserVo myAuth(HttpServletRequest request) throws IOException {
        String authorization = request.getHeader("Authorization");
        if (StringUtils.isBlank(authorization)) {
            throw new HeaderNotFoundException("Authorization");
        }
        if (!authorization.startsWith("Hmac ")) {
            throw new HeaderIrregularException("Authorization");
        }
        authorization = authorization.substring(5);
        if (StringUtils.isBlank(authorization)) {
            throw new HeaderIrregularException("Authorization");
        }
        String user = request.getHeader("x-access-key");
        if (StringUtils.isBlank(user)) {
            throw new HeaderNotFoundException("x-access-key");
        }
        /*String accessTime = request.getHeader("x-access-time");
        if (StringUtils.isBlank(accessTime)) {
            throw new HeaderNotFoundException("x-access-time");
        }
        Date accessDate = null;
        try {
            accessDate = new Date(Long.parseLong(accessTime));
        } catch (Exception ex) {
            throw new HeaderIrregularException("x-access-time");
        }
        Calendar now = Calendar.getInstance();
        now.add(Calendar.MINUTE, -expiretime);
        if (now.getTime().after(accessDate)) {
            throw new RequestExpiredException();
        }*/

        UserVo userVo = userMapper.getUserByUserId(user);

        if (userVo == null) {
            throw new UserNotFoundException(user);
        }
        String token = userMapper.getUserTokenByUserId(user);
        if (StringUtils.isBlank(token)) {
            throw new UserTokenNotFoundException(user);
        }


        InputStream input = request.getInputStream();
        StringBuilder sb = new StringBuilder();
        BufferedReader reader;
        if (input != null) {
            reader = new BufferedReader(new InputStreamReader(input));
            char[] charBuffer = new char[2048];
            int bytesRead = -1;
            while ((bytesRead = reader.read(charBuffer)) > 0) {
                sb.append(charBuffer, 0, bytesRead);
            }
        }

       /* InputStream input2 = new CachedBodyHttpServletRequest(request).getInputStream();
        StringBuilder sb2 = new StringBuilder();
        BufferedReader reader2;
        if (input2 != null) {
            reader2 = new BufferedReader(new InputStreamReader(input2));
            char[] charBuffer = new char[2048];
            int bytesRead = -1;
            while ((bytesRead = reader2.read(charBuffer)) > 0) {
                sb2.append(charBuffer, 0, bytesRead);
            }
        }*/
        String sign = user + "#" + request.getRequestURI() + "#" + Base64.encodeBase64StringUnChunked(sb.toString().getBytes(StandardCharsets.UTF_8));
        String result = SHA256Util.encrypt(token, sign);
        if (result.equalsIgnoreCase(authorization)) {
            return userVo;
        }


        return null;
    }


    @Override
    public String directUrl() {
        // TODO Auto-generated method stub
        return null;
    }


}