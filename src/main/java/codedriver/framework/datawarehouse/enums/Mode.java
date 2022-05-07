/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.datawarehouse.enums;

public enum Mode {
    APPEND("append", "追加"), REPLACE("replace", "替换");

    private final String value;
    private final String text;

    Mode(String _value, String _text) {
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
        for (Mode s : Mode.values()) {
            if (s.getValue().equals(name)) {
                return s.getText();
            }
        }
        return "";
    }
}
