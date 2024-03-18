/*Copyright (C) $today.year  深圳极向量科技有限公司 All Rights Reserved.

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
import neatlogic.framework.util.I18n;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum InspectStatus implements IEnum {
    NORMAL("normal", new I18n("正常"), "text-success"),
    WARN("warn", new I18n("告警"), "text-warning"),
    CRITICAL("critical", new I18n("严重"), "text-error"),
    FATAL("fatal", new I18n("致命"), "text-error");

    private final String value;
    private final I18n text;
    private final String cssClass;

    InspectStatus(String value, I18n text, String cssClass) {
        this.value = value;
        this.text = text;
        this.cssClass = cssClass;
    }

    public String getValue() {
        return value;
    }

    public String getText() {
        return $.t(text.toString());
    }

    public String getCssClass() {
        return cssClass;
    }


    @Override
    public String getEnumName() {
        return "巡检状态";
    }

    public static String getText(String _value) {
        for (InspectStatus s : InspectStatus.values()) {
            if (s.getValue().equalsIgnoreCase(_value)) {
                return s.getText();
            }
        }
        return "";
    }

    public static InspectStatus getInspectStatus(String _value) {
        for (InspectStatus s : InspectStatus.values()) {
            if (s.getValue().equalsIgnoreCase(_value)) {
                return s;
            }
        }
        return null;
    }

    public static JSONObject getInspectStatusJson(String value) {
        for (InspectStatus action : values()) {
            if (action.getValue().equalsIgnoreCase(value)) {
                return new JSONObject() {
                    {
                        this.put("value", action.getValue());
                        this.put("text", action.getText());
                        this.put("cssClass", action.getCssClass());
                    }
                };
            }
        }
        return null;
    }

    public static Map<String, JSONObject> getAllInspectStatusMap() {
        Map<String, JSONObject> map = new HashMap<>();
        for (InspectStatus action : values()) {
            map.put(action.getValue(), new JSONObject() {
                {
                    this.put("value", action.getValue());
                    this.put("text", action.getText());
                    this.put("cssClass", action.getCssClass());
                }
            });
        }
        return map;
    }


    @Override
    public List getValueTextList() {
        JSONArray array = new JSONArray();
        for (InspectStatus action : values()) {
            array.add(new JSONObject() {
                {
                    this.put("value", action.getValue());
                    this.put("text", action.getText());
                    this.put("cssClass", action.getCssClass());
                }
            });
        }
        return array;
    }
}
