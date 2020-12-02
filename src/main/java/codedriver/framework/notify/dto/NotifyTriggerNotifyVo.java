package codedriver.framework.notify.dto;

import java.util.List;

import codedriver.framework.dto.condition.ConditionConfigVo;

public class NotifyTriggerNotifyVo {

    private Long id;
    private List<NotifyActionVo> actionList;
    private ConditionConfigVo conditionConfig;
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public List<NotifyActionVo> getActionList() {
        return actionList;
    }
    public void setActionList(List<NotifyActionVo> actionList) {
        this.actionList = actionList;
    }
    public ConditionConfigVo getConditionConfig() {
        return conditionConfig;
    }
    public void setConditionConfig(ConditionConfigVo conditionConfig) {
        this.conditionConfig = conditionConfig;
    }
}
