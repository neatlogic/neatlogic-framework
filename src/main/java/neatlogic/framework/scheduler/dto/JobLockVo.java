/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.framework.scheduler.dto;

import neatlogic.framework.common.config.Config;

public class JobLockVo {
    public final static String RUNNING = "running";
    public final static String WAITING = "waiting";

    private String jobName;
    private String jobGroup;
    private String jobHandler;
    private String lock = WAITING;
    private Integer serverId;


    public JobLockVo() {

    }


    public JobLockVo(String _jobName, String _jobGroup, String _jobHandler) {
        jobName = _jobName;
        jobGroup = _jobGroup;
        jobHandler = _jobHandler;
    }

    public String getLock() {
        return lock;
    }

    public void setLock(String lock) {
        this.lock = lock;
    }

    public Integer getServerId() {
        if (serverId == null) {
            serverId = Config.SCHEDULE_SERVER_ID;
        }
        return serverId;
    }

    public void setServerId(Integer serverId) {
        this.serverId = serverId;
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

    public String getJobHandler() {
        return jobHandler;
    }

    public void setJobHandler(String jobHandler) {
        this.jobHandler = jobHandler;
    }
}
