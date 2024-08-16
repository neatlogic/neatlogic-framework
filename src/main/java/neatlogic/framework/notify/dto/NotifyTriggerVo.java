package neatlogic.framework.notify.dto;

import neatlogic.framework.notify.core.INotifyTriggerType;

import java.util.ArrayList;
import java.util.List;

public class NotifyTriggerVo {

    private String trigger;
    private String triggerName;
    private String description;
    private List<String> includeList = new ArrayList<>();
    private List<String> excludeList = new ArrayList<>();
    private List<NotifyTriggerNotifyVo> notifyList = new ArrayList<>();

    public NotifyTriggerVo() {}

    public NotifyTriggerVo(String trigger, String triggerName,String description) {
        this.trigger = trigger;
        this.triggerName = triggerName;
        this.description = description;
    }

    public NotifyTriggerVo(INotifyTriggerType notifyTriggerType) {
        this.trigger = notifyTriggerType.getTrigger();
        this.triggerName = notifyTriggerType.getText();
        this.description = notifyTriggerType.getDescription();
        List<String> _includeList = notifyTriggerType.getIncludeList();
        if (_includeList != null) {
            this.includeList.addAll(_includeList);
        }
        List<String> _excludeList = notifyTriggerType.getExcludeList();
        if (_excludeList != null) {
            this.excludeList.addAll(_excludeList);
        }
    }

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<NotifyTriggerNotifyVo> getNotifyList() {
        return notifyList;
    }

    public void setNotifyList(List<NotifyTriggerNotifyVo> notifyList) {
        this.notifyList = notifyList;
    }

    public void clearNotifyList() {
        this.notifyList.clear();
    }

    public List<String> getIncludeList() {
        return includeList;
    }

    public void setIncludeList(List<String> includeList) {
        this.includeList = includeList;
    }

    public List<String> getExcludeList() {
        return excludeList;
    }

    public void setExcludeList(List<String> excludeList) {
        this.excludeList = excludeList;
    }
}
