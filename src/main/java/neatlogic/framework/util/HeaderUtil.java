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
