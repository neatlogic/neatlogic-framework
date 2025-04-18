/*
 * Copyright (C) 2025  深圳极向量科技有限公司 All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package neatlogic.framework.scheduler.dto;

import java.util.Date;

public class JobInfoVo {
    private String jobName;
    private String jobGroup;
    private String cron;
    private Date beginTime;
    private Date endTime;
    private String type;
    private String jobHandler;
    private Integer needAudit;
    private String tenantUuid;
    private Integer intervalInSeconds;
    private Integer repeatCount;
    private String state;
    private Date nextFireTime;
    private Date lastFireTime;

    public JobInfoVo(){

    }

    public JobInfoVo(JobObject jobObject) {
        this.jobName = jobObject.getJobName();
        this.jobGroup = jobObject.getJobGroup();
        this.cron = jobObject.getCron();
        this.beginTime = jobObject.getBeginTime();
        this.endTime = jobObject.getEndTime();
        this.type = jobObject.getType();
        this.jobHandler = jobObject.getJobHandler();
        this.needAudit = jobObject.getNeedAudit();
        this.tenantUuid = jobObject.getTenantUuid();
        this.intervalInSeconds = jobObject.getIntervalInSeconds();
        this.repeatCount = jobObject.getRepeatCount();
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

    public Date getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getJobHandler() {
        return jobHandler;
    }

    public void setJobHandler(String jobHandler) {
        this.jobHandler = jobHandler;
    }

    public Integer getNeedAudit() {
        return needAudit;
    }

    public void setNeedAudit(Integer needAudit) {
        this.needAudit = needAudit;
    }

    public String getTenantUuid() {
        return tenantUuid;
    }

    public void setTenantUuid(String tenantUuid) {
        this.tenantUuid = tenantUuid;
    }

    public Integer getIntervalInSeconds() {
        return intervalInSeconds;
    }

    public void setIntervalInSeconds(Integer intervalInSeconds) {
        this.intervalInSeconds = intervalInSeconds;
    }

    public Integer getRepeatCount() {
        return repeatCount;
    }

    public void setRepeatCount(Integer repeatCount) {
        this.repeatCount = repeatCount;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Date getNextFireTime() {
        return nextFireTime;
    }

    public void setNextFireTime(Date nextFireTime) {
        this.nextFireTime = nextFireTime;
    }

    public Date getLastFireTime() {
        return lastFireTime;
    }

    public void setLastFireTime(Date lastFireTime) {
        this.lastFireTime = lastFireTime;
    }
}
