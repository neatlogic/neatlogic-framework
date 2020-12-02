package codedriver.framework.notify.dto;

import java.util.List;

import codedriver.framework.dto.ConditionParamVo;
import codedriver.framework.dto.UserVo;

public class NotifyPolicyConfigVo {

    private List<String> adminUserUuidList;
    private List<NotifyTriggerVo> triggerList;
    private List<UserVo> userList;
    private List<ConditionParamVo> paramList;
    private List<NotifyTemplateVo> templateList;
    private List<ConditionParamVo> conditionOptionList;
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
    public List<UserVo> getUserList() {
        return userList;
    }
    public void setUserList(List<UserVo> userList) {
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
