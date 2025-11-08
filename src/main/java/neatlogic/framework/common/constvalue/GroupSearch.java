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

package neatlogic.framework.common.constvalue;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.util.$;

import java.util.List;


public enum GroupSearch implements IEnum {
    USER("user", "common.user"),
    TEAM("team", "common.group"),
    ROLE("role", "common.role"),
    COMMON("common", "common.common");

    private final String value;
    private final String text;

    private GroupSearch(String value, String text) {
        this.value = value;
        this.text = text;
    }

    public String getValue() {
        return value;
    }

    public String getValuePlugin() {
        return value + "#";
    }

    public String addPrefix(String uuid) {
        return getValuePlugin() + uuid;
    }

    public String getText() {
        return $.t(text);
    }

    public static String getValue(String _value) {
        for (GroupSearch gs : GroupSearch.values()) {
            if (gs.value.equals(_value)) {
                return gs.value;
            }
        }
        return null;
    }

    public static GroupSearch getGroupSearch(String _value) {
        for (GroupSearch gs : GroupSearch.values()) {
            if (gs.value.equals(_value)) {
                return gs;
            }
        }
        return null;
    }

    public static String removePrefix(String _value) {
        for (GroupSearch gs : GroupSearch.values()) {
            if (_value.startsWith(gs.getValuePlugin())) {
                return _value.substring(gs.getValuePlugin().length());
            }
        }
        return _value;
    }

    public static String getPrefix(String _value) {
        for (GroupSearch gs : GroupSearch.values()) {
            if (_value.startsWith(gs.getValuePlugin())) {
                return gs.value;
            }
        }
        return null;
    }


    @Override
    public List getValueTextList() {
        JSONArray array = new JSONArray();
        for (GroupSearch groupSearch : GroupSearch.values()) {
            array.add(new JSONObject() {
                {
                    this.put("value", groupSearch.getValue());
                    this.put("text", groupSearch.getText());
                }
            });
        }
        return array;
    }
}
