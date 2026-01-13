/*
 * Copyright (C) 2026  深圳极向量科技有限公司 All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package neatlogic.module.framework.filter.handler;

import neatlogic.framework.common.constvalue.systemuser.SystemUser;
import neatlogic.framework.dto.UserVo;
import neatlogic.framework.exception.auth.NotSupportBasicAuthException;
import neatlogic.framework.exception.hmac.HeaderIrregularException;
import neatlogic.framework.exception.hmac.HeaderNotFoundException;
import neatlogic.framework.exception.type.ApiNotDefinedException;
import neatlogic.framework.exception.type.ApiNotFoundException;
import neatlogic.framework.filter.core.LoginAuthHandlerBase;
import neatlogic.framework.restful.core.privateapi.PrivateApiComponentFactory;
import neatlogic.framework.restful.dao.mapper.ApiMapper;
import neatlogic.framework.restful.dto.ApiVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;
import java.util.Set;

@Service
public class BasicAuthHandler extends LoginAuthHandlerBase {
    private static final Set<String> CHANNELS = Set.of("rest", "stream", "binary");

    @Resource
    ApiMapper apiMapper;

    @Override
    public UserVo myAuth(HttpServletRequest request) throws Exception {
        String authorization = request.getHeader("Authorization");

        if (StringUtils.isBlank(authorization)) {
            throw new HeaderNotFoundException("Authorization");
        }

        if (!authorization.startsWith("Basic ")) {
            throw new HeaderIrregularException("Authorization");
        }

        // 去掉 "Basic "
        String base64Credentials = authorization.substring(6).trim();
        if (StringUtils.isBlank(base64Credentials)) {
            throw new HeaderIrregularException("Authorization");
        }

        String credentials;
        try {
            credentials = new String(
                    Base64.getDecoder().decode(base64Credentials),
                    StandardCharsets.UTF_8
            );
        } catch (IllegalArgumentException e) {
            throw new HeaderIrregularException("Authorization");
        }

        // 格式必须是 username:password
        int separatorIndex = credentials.indexOf(':');
        if (separatorIndex <= 0) {
            throw new HeaderIrregularException("Authorization");
        }

        String username = credentials.substring(0, separatorIndex);
        String password = credentials.substring(separatorIndex + 1);

        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            throw new HeaderIrregularException("Authorization");
        }

        String token = extractToken(request);
        ApiVo apiVo = apiMapper.getApiByToken(token);
        if (apiVo == null) {
            throw new ApiNotDefinedException(token);
        }
        ApiVo interfaceVo = PrivateApiComponentFactory.getApiByToken(token);
        if (interfaceVo == null) {
            throw new ApiNotFoundException(token);
        }

        if (!interfaceVo.getBasicSupport()) {
            throw new NotSupportBasicAuthException();
        }

        if (!Objects.equals(apiVo.getUsername(), username) || !Objects.equals(apiVo.getPassword(), password)) {
            return null;
        }
        return SystemUser.SYSTEM.getUserVo();
    }

    @Override
    public String getType() {
        return "basic";
    }

    /**
     * 从 HttpServletRequest 中解析 API token
     *
     * URL 规范：
     *   /{contextPath}/api/{channel}/{token...}
     *
     * 返回：
     *   null              → 非 API 请求或非法路径
     *   "" (空字符串)     → /api/{channel}（极少见）
     *   "user/create"     → /api/rest/user/create
     */
    public static String extractToken(HttpServletRequest request) {
        if (request == null) {
            return null;
        }

        String uri = request.getRequestURI();          // /neatlogic/api/rest/user/create
        String contextPath = request.getContextPath(); // /neatlogic

        if (StringUtils.isBlank(uri)) {
            return null;
        }

        // 去掉 contextPath
        if (StringUtils.isNotBlank(contextPath) && uri.startsWith(contextPath)) {
            uri = uri.substring(contextPath.length());
        }

        // 必须是 /api/xxx
        if (!uri.startsWith("/api/")) {
            return null;
        }

        // 去掉 "/api/"
        String remain = uri.substring(5); // rest/user/create

        int idx = remain.indexOf('/');
        if (idx <= 0) {
            return null;
        }

        String channel = remain.substring(0, idx);
        if (!CHANNELS.contains(channel)) {
            return null;
        }

        // 剩余部分即 token（支持多级）
        return remain.substring(idx + 1);
    }

    public static void main(String[] args) {
        String username = "lvzk";
        String password = "123456";
        if (StringUtils.isNotBlank(username) && StringUtils.isNotBlank(password)) {
            Base64.Encoder encoder = Base64.getEncoder();
            String key = username + ":" + password;
            System.out.println("Basic " + encoder.encodeToString(key.getBytes()));
        }
    }
}

