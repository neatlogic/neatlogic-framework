package neatlogic.framework.dto.loginaudit;

import neatlogic.framework.common.dto.BasePageVo;

import java.io.Serial;
import java.util.Date;
import java.util.List;

public class LoginAuditSearchVo extends BasePageVo {
    @Serial
    private static final long serialVersionUID = 5271912550863395508L;
    private Date startTime;
    private Date endTime;
    private List<String> teamUuidList;
    private List<String> moduleGroupList;
    private List<String> featureNameList;

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public List<String> getTeamUuidList() {
        return teamUuidList;
    }

    public void setTeamUuidList(List<String> teamUuidList) {
        this.teamUuidList = teamUuidList;
    }

    public List<String> getModuleGroupList() {
        return moduleGroupList;
    }

    public void setModuleGroupList(List<String> moduleGroupList) {
        this.moduleGroupList = moduleGroupList;
    }

    public List<String> getFeatureNameList() {
        return featureNameList;
    }

    public void setFeatureNameList(List<String> featureNameList) {
        this.featureNameList = featureNameList;
    }
}
