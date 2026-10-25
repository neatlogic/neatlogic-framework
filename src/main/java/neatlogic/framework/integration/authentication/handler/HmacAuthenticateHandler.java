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

package neatlogic.framework.integration.authentication.handler;

import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.asynchronization.threadlocal.UserContext;
import neatlogic.framework.dao.mapper.UserMapper;
import neatlogic.framework.dto.UserVo;
import neatlogic.framework.exception.integration.AuthenticateException;
import neatlogic.framework.exception.user.UserNotFoundException;
import neatlogic.framework.exception.user.UserTokenNotFoundException;
import neatlogic.framework.integration.authentication.core.IAuthenticateHandler;
import neatlogic.framework.integration.authentication.enums.AuthenticateType;
import neatlogic.framework.service.UserService;
import neatlogic.framework.util.SHA256Util;
import neatlogic.framework.util.SpringContextUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.util.Base64;

import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/** 集成请求使用当前用户或指定用户的接口 Token 生成内部 Hmac 签名。 */
public class HmacAuthenticateHandler implements IAuthenticateHandler {
    public static final String CURRENT_USER = "common#loginuser";

    @Override
    public String getType() {
        return AuthenticateType.HMAC.getValue();
    }

    /** 保存和运行共用校验，原始值必须是单个用户字符串，不能接受数组隐式转换。 */
    public static void validateConfig(JSONObject config) {
        Object value = config == null ? null : config.get("user");
        if (!(value instanceof String)) {
            throw new AuthenticateException("auth.hmacuserinvalid");
        }
        String user = (String) value;
        if (CURRENT_USER.equals(user)) {
            return;
        }
        if (!user.startsWith("user#") || StringUtils.isBlank(user.substring(5))
                || user.substring(5).contains("#")) {
            throw new AuthenticateException("auth.hmacuserinvalid");
        }
        getSelectedUser(user.substring(5));
    }

    /** 指定用户必须仍然存在；沿用已有 Mapper，不新增 SQL。 */
    private static UserVo getSelectedUser(String uuid) {
        UserVo user = SpringContextUtil.getBean(UserMapper.class).getUserByUuid(uuid);
        if (user == null || StringUtils.isBlank(user.getUserId())) {
            throw new UserNotFoundException(uuid);
        }
        return user;
    }

    /** 无正文上下文的旧调用方不得静默生成不完整签名。 */
    @Override
    public void authenticate(HttpURLConnection connection, JSONObject config) {
        validateConfig(config);
        String selected = config.getString("user");
        UserService service = SpringContextUtil.getBean(UserService.class);
        String userId;
        if (CURRENT_USER.equals(selected)) {
            UserContext context = UserContext.get();
            if (context == null || StringUtils.isBlank(context.getUserId())) {
                throw new AuthenticateException("auth.hmaccurrentusermissing");
            }
            userId = context.getUserId();
            if (service.getUserByUser(userId) == null) {
                throw new UserNotFoundException(userId);
            }
        } else {
            userId = getSelectedUser(selected.substring(5)).getUserId();
        }
        String token = service.getUserTokenByUser(userId);
        if (StringUtils.isBlank(token)) {
            throw new UserTokenNotFoundException(userId);
        }
        String inputParam = StringUtils.EMPTY;
        if (!Objects.equals(connection.getRequestMethod().toUpperCase(), "GET")) {
            inputParam = config.getString("inputParam");
        }
        String signature = sign(token, userId, connection.getURL().getPath(), connection.getURL().getQuery(), inputParam);
        connection.setRequestProperty("AuthType", "hmac");
        connection.setRequestProperty("x-access-key", userId);
        connection.setRequestProperty("Authorization", "Hmac " + signature);
        connection.setRequestProperty("Tenant", TenantContext.get().getTenantUuid());
    }

    /** URI 和查询串保留原始编码；正文按现有接收端 UTF-8 文本规则处理。 */
    private String sign(String token, String user, String uri, String query, String body) {
        String queryString = StringUtils.isNotBlank(query) ? "?" + query : StringUtils.EMPTY;
        String content = user + "#" + uri + queryString + "#"
                + Base64.encodeBase64StringUnChunked(body.getBytes(StandardCharsets.UTF_8));
        return SHA256Util.encrypt(token, content);
    }
}
