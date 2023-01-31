/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.framework.datawarehouse.enums;

public enum FieldInputType {
    TEXT("text", "文本框"), DATETIME("datetime", "日期时间"),
    SELECT("select", "下拉框"), RADIO("radio", "单选框"),
    CHECKBOX("checkbox", "复选框");

    private final String value;
    private final String text;

    FieldInputType(String _value, String _text) {
        this.value = _value;
        this.text = _text;
    }

    public String getValue() {
        return value;
    }

    public String getText() {
        return text;
    }

    public static String getValue(String _status) {
        for (FieldInputType s : FieldInputType.values()) {
            if (s.getValue().equals(_status)) {
                return s.getValue();
            }
        }
        return null;
    }

    public static String getText(String name) {
        for (FieldInputType s : FieldInputType.values()) {
            if (s.getValue().equals(name)) {
                return s.getText();
            }
        }
        return "";
    }
}
