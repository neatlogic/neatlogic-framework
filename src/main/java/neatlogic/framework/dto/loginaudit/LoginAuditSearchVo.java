package neatlogic.framework.dto.loginaudit;

import neatlogic.framework.common.dto.BasePageVo;

import java.util.Date;

public class LoginAuditSearchVo extends BasePageVo {
    private Date startTime;
    private Date endTime;

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
}
