/*
 * Copyright (C) 2025  深圳极向量科技有限公司 All Rights Reserved.
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

package neatlogic.framework.util;

import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.asynchronization.threadlocal.RequestContext;
import neatlogic.framework.asynchronization.threadlocal.UserContext;
import neatlogic.framework.common.config.Config;
import org.apache.commons.collections4.MapUtils;

import java.util.Enumeration;
import java.util.Set;
import java.util.stream.Collectors;

public class HeaderUtil {
    private HeaderUtil() {
        throw new IllegalStateException("Utility class");
    }
    /**
     * 获取满足规则的请求头集合
     * @param headerSet 请求头集合
     */
    public static void getRulePrefixHeader(Set<String> headerSet){
        JSONObject headers = getHeaders();
        if (MapUtils.isNotEmpty(headers)) {
            headerSet.addAll(headers.keySet().stream().filter(o -> o.startsWith(Config.HEADER_RULE_PREFIX())).collect(Collectors.toSet()));
        }
    }

    /**
     * 获取用户上下文中或请求中的headers
     */
    public static JSONObject getHeaders() {
        JSONObject headers = new JSONObject();
        if (UserContext.get() != null && UserContext.get().getJwtVo() != null) {
            headers = UserContext.get().getJwtVo().getHeaders();
        } else if (RequestContext.get() != null && RequestContext.get().getRequest() != null) {
            Enumeration<String> envNames = RequestContext.get().getRequest().getHeaderNames();
            while (envNames != null && envNames.hasMoreElements()) {
                String key = envNames.nextElement();
                String value = RequestContext.get().getRequest().getHeader(key);
                headers.put(key, value);
            }
        }
        return headers;
    }
}
