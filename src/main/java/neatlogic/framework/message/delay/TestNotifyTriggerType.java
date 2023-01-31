package neatlogic.framework.message.delay;

import neatlogic.framework.notify.core.INotifyTriggerType;

/**
 * @Title: TestNotifyTriggerType
 * @Package neatlogic.framework.message.delay
 * @Description: 测试触发点，用于压测
 * @Author: linbq
 * @Date: 2021/1/12 7:49
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 **/
public enum TestNotifyTriggerType implements INotifyTriggerType {

    TEST("test", "测试触发点", "用于测试消息缓存");

    private String trigger;
    private String text;
    private String description;

    private TestNotifyTriggerType(String _trigger, String _text, String _description) {
        this.trigger = _trigger;
        this.text = _text;
        this.description = _description;
    }

    @Override
    public String getTrigger() {
        return null;
    }

    @Override
    public String getText() {
        return null;
    }

    @Override
    public String getDescription() {
        return null;
    }
}
