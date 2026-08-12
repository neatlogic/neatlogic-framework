/*
 *
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the NeatLogic Sustainable Use License (NSUL), Version 4.x - 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */

package neatlogic.framework.scheduler.dto;

import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.common.dto.BaseEditorVo;
import neatlogic.framework.restful.annotation.EntityField;

/**
 * 定时作业来源信息。
 * 既承载schedule_job_source表的主键和服务器归属，也提供管理页需要的作业显示信息。
 */
public class ScheduleJobSourceVo extends BaseEditorVo {
    private static final long serialVersionUID = -5352259213757469975L;

    @EntityField(name = "作业唯一标识", type = ApiParamType.STRING)
    private String jobName;

    @EntityField(name = "作业组", type = ApiParamType.STRING)
    private String jobGroup;

    @EntityField(name = "作业处理器类路径", type = ApiParamType.STRING)
    private String handler;

    @EntityField(name = "作业模块", type = ApiParamType.STRING)
    private String handlerName;

    @EntityField(name = "服务器ID", type = ApiParamType.INTEGER)
    private Integer serverId;

    @EntityField(name = "服务器组", type = ApiParamType.STRING)
    private String serverGroup;

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

    public String getHandler() {
        return handler;
    }

    public void setHandler(String handler) {
        this.handler = handler;
    }

    public String getHandlerName() {
        return handlerName;
    }

    public void setHandlerName(String handlerName) {
        this.handlerName = handlerName;
    }

    public Integer getServerId() {
        return serverId;
    }

    public void setServerId(Integer serverId) {
        this.serverId = serverId;
    }

    public String getServerGroup() {
        return serverGroup;
    }

    public void setServerGroup(String serverGroup) {
        this.serverGroup = serverGroup;
    }
}
