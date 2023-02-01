package neatlogic.framework.notify.dto;

import java.util.ArrayList;
import java.util.List;

import neatlogic.framework.dto.condition.ConditionConfigVo;

public class NotifyTriggerNotifyVo {

    private Long id;
    private List<NotifyActionVo> actionList = new ArrayList<>();
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
