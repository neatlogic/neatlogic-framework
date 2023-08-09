/*
 * Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package neatlogic.framework.common.constvalue;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.util.$;
import neatlogic.framework.util.I18n;

import java.util.List;


public enum GroupSearch implements IEnum {
    USER("user", new I18n("用户")),
    TEAM("team", new I18n("分组")),
    ROLE("role", new I18n("角色")),
    COMMON("common", new I18n("公共"));

    private final String value;
    private final I18n text;

    private GroupSearch(String value, I18n text) {
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
        return $.t(text.toString());
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
