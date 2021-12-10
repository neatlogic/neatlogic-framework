/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.notify.core;

public enum NotifyHandlerType {

    EMAIL("email", "邮件通知"),
    MESSAGE("message", "消息通知");

    private final String value;
    private final String text;

    NotifyHandlerType(String value, String text) {
        this.value = value;
        this.text = text;
    }

    public String getValue() {
        return value;
    }

    public String getText() {
        return text;
    }

    public static String getText(String _value) {
        for (NotifyHandlerType n : NotifyHandlerType.values()) {
            if (n.getValue().equals(_value)) {
                return n.getText();
            }
        }
        return "";
    }
}
