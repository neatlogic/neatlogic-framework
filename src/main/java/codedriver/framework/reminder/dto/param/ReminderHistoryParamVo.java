package codedriver.framework.reminder.dto.param;

import codedriver.framework.common.dto.BasePageVo;

import java.util.List;

/**
 * @program: codedriver
 * @description: 实时动态历史查询Vo
 * @create: 2020-03-09 15:52
 **/
public class ReminderHistoryParamVo extends BasePageVo {
    private String moduleId;
    private String startTime;
    private String endTime;
    private String userUuid;
    private List<String> pluginIdList;

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getUserUuid() {
        return userUuid;
    }

    public void setUserUuid(String userUuid) {
        this.userUuid = userUuid;
    }

    public List<String> getPluginIdList() {
        return pluginIdList;
    }

    public void setPluginIdList(List<String> pluginIdList) {
        this.pluginIdList = pluginIdList;
    }
}
