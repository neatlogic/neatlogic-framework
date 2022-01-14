/*
 * Copyright (c)  2021 TechSure Co.,Ltd.  All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.common.constvalue;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum InspectStatus implements IEnum {
    NORMAL("normal", "正常", "text-success"),
    WARN("warn", "告警", "text-warning"),
    CRITICAL("critical", "严重", "text-error"),
    FATAL("fatal", "致命", "text-error");

    private final String value;
    private final String text;
    private final String cssClass;

    InspectStatus(String value, String text, String cssClass) {
        this.value = value;
        this.text = text;
        this.cssClass = cssClass;
    }

    public String getValue() {
        return value;
    }

    public String getText() {
        return text;
    }

    public String getCssClass() {
        return cssClass;
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

    public static Map<String,JSONObject> getAllInspectStatusMap() {
        Map<String,JSONObject> map = new HashMap<>();
        for (InspectStatus action : values()) {
            map.put(action.getValue(),new JSONObject() {
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
