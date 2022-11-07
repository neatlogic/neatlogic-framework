/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.datawarehouse.enums;

public enum Status {
    DOING("doing", "同步数据中"), DONE("done", "同步完成"), FAILED("failed", "同步失败");

    private final String value;
    private final String text;

    Status(String _value, String _text) {
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
        for (Status s : Status.values()) {
            if (s.getValue().equals(name)) {
                return s.getText();
            }
        }
        return "";
    }
}
