/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.framework.datawarehouse.enums;

public enum FieldType {
    TEXT("text", "文本"), DATETIME("datetime", "日期时间"),
    DATE("date", "日期"), TIME("time", "时间"),
    NUMBER("number", "数字");

    private final String value;
    private final String text;

    FieldType(String _value, String _text) {
        this.value = _value;
        this.text = _text;
    }

    public String getValue() {
        return value;
    }

    public String getText() {
        return text;
    }


    public static String getText(String name) {
        for (FieldType s : FieldType.values()) {
            if (s.getValue().equals(name)) {
                return s.getText();
            }
        }
        return "";
    }
}
