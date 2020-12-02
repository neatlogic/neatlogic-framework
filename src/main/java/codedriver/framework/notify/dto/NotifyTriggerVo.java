package codedriver.framework.notify.dto;

import java.util.List;

public class NotifyTriggerVo {

    private String trigger;
    private String triggerName;
    private List<NotifyTriggerNotifyVo> notifyList;
    public String getTrigger() {
        return trigger;
    }
    public void setTrigger(String trigger) {
        this.trigger = trigger;
    }
    public String getTriggerName() {
        return triggerName;
    }
    public void setTriggerName(String triggerName) {
        this.triggerName = triggerName;
    }
    public List<NotifyTriggerNotifyVo> getNotifyList() {
        return notifyList;
    }
    public void setNotifyList(List<NotifyTriggerNotifyVo> notifyList) {
        this.notifyList = notifyList;
    }
}
