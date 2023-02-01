/*
 * Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
