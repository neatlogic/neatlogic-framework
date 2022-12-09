/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.scheduler.dto;

import java.util.Date;

@Deprecated
public class JobLoadTimeVo {
    private String jobName;
    private String jobGroup;
    private String cron;
    private Date loadTime;

    public JobLoadTimeVo() {

    }

    public JobLoadTimeVo(String jobName, String jobGroup) {
        this(jobName, jobGroup, null, null);
    }

    public JobLoadTimeVo(String jobName, String jobGroup, String cron, Date loadTime) {
        this.jobName = jobName;
        this.jobGroup = jobGroup;
        this.cron = cron;
        this.loadTime = loadTime;
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

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public Date getLoadTime() {
        return loadTime;
    }

    public void setLoadTime(Date loadTime) {
        this.loadTime = loadTime;
    }
}
