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

package neatlogic.framework.restful.enums;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.common.constvalue.IEnum;

import java.util.List;

/**
 * API 审计访问类型，用于区分 REST 与 MCP 工具调用。
 */
public enum ApiAccessType implements IEnum {
    REST("rest", "REST"),
    MCP("mcp", "MCP");

    /**
     * MCP 分派器写入请求对象的服务端属性名，客户端无法通过请求参数覆盖。
     */
    public static final String REQUEST_ATTRIBUTE = ApiAccessType.class.getName();

    private final String value;
    private final String text;

    ApiAccessType(String value, String text) {
        this.value = value;
        this.text = text;
    }

    @Override
    public String getValue() {
        return value;
    }

    public String getText() {
        return text;
    }

    /**
     * 将请求属性转换为合法访问类型，缺失或非法值统一按 REST 处理。
     */
    public static String normalize(Object value) {
        if (value != null) {
            String type = value.toString();
            for (ApiAccessType accessType : values()) {
                if (accessType.getValue().equals(type)) {
                    return accessType.getValue();
                }
            }
        }
        return REST.getValue();
    }

    /**
     * 返回通用枚举接口使用的访问类型选项。
     */
    @Override
    public List getValueTextList() {
        JSONArray resultList = new JSONArray();
        for (ApiAccessType accessType : values()) {
            JSONObject result = new JSONObject();
            result.put("value", accessType.getValue());
            result.put("text", accessType.getText());
            resultList.add(result);
        }
        return resultList;
    }
}
