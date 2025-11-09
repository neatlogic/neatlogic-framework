/*
 *
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the NeatLogic Sustainable Use License (NSUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */

package neatlogic.framework.scheduler.dto;

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
