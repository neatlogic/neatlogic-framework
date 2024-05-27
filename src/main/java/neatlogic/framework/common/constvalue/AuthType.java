/*Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.*/

package neatlogic.framework.common.constvalue;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.util.$;

import java.util.List;


public enum AuthType implements IEnum {
    USER("user", "common.user"),
    TEAM("team", "common.team"),
    ROLE("role", "common.role"),
    COMMON("common", "common.embed");

    private final String value;
    private final String text;

    AuthType(String value, String text) {
        this.value = value;
        this.text = text;
    }

    public String getValue() {
        return value;
    }

    public String getValuePlugin() {
        return value + "#";
    }

    public String getText() {
        return $.t(text);
    }

    public static String getValue(String _value) {
        for (AuthType gs : AuthType.values()) {
            if (gs.value.equals(_value)) {
                return gs.value;
            }
        }
        return null;
    }

    public static AuthType getGroupSearch(String _value) {
        for (AuthType gs : AuthType.values()) {
            if (gs.value.equals(_value)) {
                return gs;
            }
        }
        return null;
    }

    public static String removePrefix(String _value) {
        for (AuthType gs : AuthType.values()) {
            if (_value.startsWith(gs.getValuePlugin())) {
                return _value.substring(gs.getValuePlugin().length());
            }
        }
        return _value;
    }

    public static String getPrefix(String _value) {
        for (AuthType gs : AuthType.values()) {
            if (_value.startsWith(gs.getValuePlugin())) {
                return gs.getValue();
            }
        }
        return null;
    }


    @Override
    public List getValueTextList() {
        JSONArray array = new JSONArray();
        for (AuthType groupSearch : AuthType.values()) {
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
