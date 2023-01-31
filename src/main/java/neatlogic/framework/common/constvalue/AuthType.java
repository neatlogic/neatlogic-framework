/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.framework.common.constvalue;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.List;


public enum AuthType implements IEnum {
    USER("user", "用户"),
    TEAM("team", "组"),
    ROLE("role", "角色"),
    COMMON("common", "内置");

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
        return text;
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
