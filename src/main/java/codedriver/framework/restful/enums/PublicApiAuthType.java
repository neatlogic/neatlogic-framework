/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */
package codedriver.framework.restful.enums;

import codedriver.framework.common.constvalue.IEnum;
import codedriver.framework.restful.auth.core.ApiAuthFactory;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.List;

public enum PublicApiAuthType implements IEnum {
    BASIC("basic", "Basic认证");

    private final String type;
    private final String text;

    PublicApiAuthType(String _type, String _text) {
        this.type = _type;
        this.text = _text;
    }

    public String getValue() {
        return this.type;
    }

    public String getText() {
        return this.text;
    }

    public static String getText(String value) {
        for (PublicApiAuthType type : PublicApiAuthType.values()) {
            if (type.getValue().equals(value)) {
                return type.getText();
            }
        }
        return "";
    }

    public static PublicApiAuthType getAuthenticateType(String value) {
        for (PublicApiAuthType type : PublicApiAuthType.values()) {
            if (type.getValue().equals(value)) {
                return type;
            }
        }
        return null;
    }


    @Override
    public List getValueTextList() {
        JSONArray array = new JSONArray();
        for (PublicApiAuthType type : PublicApiAuthType.values()) {
            array.add(new JSONObject() {
                {
                    this.put("value", type.getValue());
                    this.put("text", type.getText());
                    this.put("help", ApiAuthFactory.getApiAuth(type.getValue()).help());
                }
            });
        }
        return array;
    }
}