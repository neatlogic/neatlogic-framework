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

import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.dto.UserVo;
import neatlogic.framework.exception.hmac.HeaderIrregularException;
import neatlogic.framework.exception.hmac.HeaderNotFoundException;
import neatlogic.framework.exception.user.UserNotFoundException;
import neatlogic.framework.exception.user.UserTokenNotFoundException;
import neatlogic.framework.filter.core.LoginAuthHandlerBase;
import neatlogic.framework.service.UserService;
import neatlogic.framework.util.SHA256Util;
import org.apache.commons.fileupload.FileItem;
import neatlogic.framework.filter.request.ParsedMultipartRequest;
import neatlogic.framework.exception.request.RequestBodyException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private final Logger logger = LoggerFactory.getLogger(HmacLoginAuthHandler.class);


    @Resource
    private UserService userService;

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

        UserVo userVo = userService.getUserByUser(user);
        if (userVo == null) {
            throw new UserNotFoundException(user);
        }
        String token = userService.getUserTokenByUser(user);
        if (StringUtils.isBlank(token)) {
            throw new UserTokenNotFoundException(user);
        }

        //System.out.println(request.getContentType());
        String bodyJsonString = StringUtils.EMPTY;
        JSONObject bodyJson = new JSONObject();
        if (ServletFileUpload.isMultipartContent(request)) {
            if (!(request instanceof ParsedMultipartRequest)) {
                throw new RequestBodyException(500, "shared-multipart-required", request.getRequestURI(), null);
            }
            // 签名保留原 FileItem 解码及重复字段最后值覆盖语义，不使用合并后的业务参数。
            for (FileItem item : ((ParsedMultipartRequest) request).getFormItems()) {
                bodyJson.put(item.getFieldName(), item.getString());
            }
            bodyJsonString = bodyJson.toString();
        } else {
            InputStream input = request.getInputStream();
            StringBuilder sb = new StringBuilder();
            BufferedReader reader;
            if (input != null) {
                reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8));
                char[] charBuffer = new char[2048];
                int bytesRead = -1;
                while ((bytesRead = reader.read(charBuffer)) > 0) {
                    sb.append(charBuffer, 0, bytesRead);
                }
            }
            bodyJsonString = sb.toString();
        }

        String queryString = StringUtils.isNotBlank(request.getQueryString()) ? "?" + request.getQueryString() : StringUtils.EMPTY;
        String sign = user + "#" + request.getRequestURI() + queryString + "#" + Base64.encodeBase64StringUnChunked(bodyJsonString.getBytes(StandardCharsets.UTF_8));
        String result = SHA256Util.encrypt(token, sign);
        if (result.equalsIgnoreCase(authorization)) {
            return userVo;
        }
        // 验签失败时保留签名输入与计算结果，便于开启 DEBUG 后排查客户端签名差异。
        if (logger.isDebugEnabled()) {
            logger.debug("header Authorization = {}", request.getHeader("Authorization"));
            logger.debug("header x-access-key = {}", request.getHeader("x-access-key"));
            logger.debug("request uri = {}", request.getRequestURI());
            logger.debug("token = {}", token);
            logger.debug("bodyJsonString = {}", bodyJsonString);
            logger.debug("queryString = {}", queryString);
            logger.debug("sign = {}", sign);
            logger.debug("result = {}", result);
        }
        return null;
    }

    /** 上传签名仅需要普通表单项，允许认证与 Dispatcher 复用同一次解析。 */
    @Override
    public boolean supportsParsedMultipart() {
        return true;
    }

    @Override
    public boolean isNeedCookie() {
        return false;
    }
}
