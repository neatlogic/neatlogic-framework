package codedriver.framework.notify.dto;

import java.util.ArrayList;
import java.util.List;

import codedriver.framework.dto.ConditionParamVo;

public class NotifyPolicyConfigVo {

    private List<String> adminUserUuidList = new ArrayList<>();
    private List<NotifyTriggerVo> triggerList = new ArrayList<>();
    private List<String> userList = new ArrayList<>();
    private List<ConditionParamVo> paramList = new ArrayList<>();
    private List<NotifyTemplateVo> templateList = new ArrayList<>();
    private List<ConditionParamVo> conditionOptionList = new ArrayList<>();

    public List<String> getAdminUserUuidList() {
        return adminUserUuidList;
    }

    public void setAdminUserUuidList(List<String> adminUserUuidList) {
        this.adminUserUuidList = adminUserUuidList;
    }

    public List<NotifyTriggerVo> getTriggerList() {
        return triggerList;
    }

    public void setTriggerList(List<NotifyTriggerVo> triggerList) {
        this.triggerList = triggerList;
    }

    public List<String> getUserList() {
        return userList;
    }

    public void setUserList(List<String> userList) {
        this.userList = userList;
    }

    public List<ConditionParamVo> getParamList() {
        return paramList;
    }

    public void setParamList(List<ConditionParamVo> paramList) {
        this.paramList = paramList;
    }

    public List<NotifyTemplateVo> getTemplateList() {
        return templateList;
    }

    public void setTemplateList(List<NotifyTemplateVo> templateList) {
        this.templateList = templateList;
    }

    public List<ConditionParamVo> getConditionOptionList() {
        return conditionOptionList;
    }

    public void setConditionOptionList(List<ConditionParamVo> conditionOptionList) {
        this.conditionOptionList = conditionOptionList;
    }
}
