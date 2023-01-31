/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.module.framework.notify.exception;

import neatlogic.framework.notify.core.INotifyTriggerType;

public enum ExceptionNotifyTriggerType implements INotifyTriggerType {

    EMAILNOTIFYEXCEPTION("emailnotifyexception", "邮件通知异常", "邮件通知发生异常时触发通知");

    private final String trigger;
    private final String text;
    private final String description;

    ExceptionNotifyTriggerType(String _trigger, String _text, String _description) {
        this.trigger = _trigger;
        this.text = _text;
        this.description = _description;
    }

    @Override
    public String getTrigger() {
        return trigger;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public static String getText(String trigger) {
        for (ExceptionNotifyTriggerType n : values()) {
            if (n.getTrigger().equals(trigger)) {
                return n.getText();
            }
        }
        return "";
    }
}
