/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.scheduler.dto;

import java.util.Date;

public class JobLoadTimeVo {
    private String jobName;
    private String jobGroup;
    private Date lcd;

    public JobLoadTimeVo() {

    }

    public JobLoadTimeVo(String jobName, String jobGroup) {
        this(jobName, jobGroup, null);
    }

    public JobLoadTimeVo(String jobName, String jobGroup, Date lcd) {
        this.jobName = jobName;
        this.jobGroup = jobGroup;
        this.lcd = lcd;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getJobGroup() {
        return jobGroup;
    }

    public void setJobGroup(String jobGroup) {
        this.jobGroup = jobGroup;
    }

    public Date getLcd() {
        return lcd;
    }

    public void setLcd(Date lcd) {
        this.lcd = lcd;
    }
}
