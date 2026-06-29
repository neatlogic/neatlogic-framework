/*
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the NeatLogic Sustainable Use License (NSUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */

package neatlogic.framework.dto.featureusageaudit;

import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.restful.annotation.EntityField;
import neatlogic.framework.util.SnowflakeUtil;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

public class FeatureUsageAuditVo implements Serializable {

    @Serial
    private static final long serialVersionUID = -3637843643859009471L;

    @EntityField(name = "主键ID", type = ApiParamType.LONG)
    private Long id;

    @EntityField(name = "用户UUID", type = ApiParamType.STRING)
    private String userUuid;

    @EntityField(name = "模块组", type = ApiParamType.STRING)
    private String moduleGroup;

    @EntityField(name = "模块组名称", type = ApiParamType.STRING)
    private String moduleGroupName;

    @EntityField(name = "功能路径", type = ApiParamType.STRING)
    private String featurePath;

    @EntityField(name = "功能名称", type = ApiParamType.STRING)
    private String featureName;

    @EntityField(name = "开始时间", type = ApiParamType.LONG)
    private Date startTime;

    @EntityField(name = "结束时间", type = ApiParamType.LONG)
    private Date endTime;

    @EntityField(name = "使用时长", type = ApiParamType.LONG)
    private Long duration;

    @EntityField(name = "登录记录ID", type = ApiParamType.LONG)
    private Long loginAuditId;

    @EntityField(name = "使用次数", type = ApiParamType.LONG)
    private Long usedCount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserUuid() {
        return userUuid;
    }

    public void setUserUuid(String userUuid) {
        this.userUuid = userUuid;
    }

    public String getModuleGroup() {
        return moduleGroup;
    }

    public void setModuleGroup(String moduleGroup) {
        this.moduleGroup = moduleGroup;
    }

    public String getModuleGroupName() {
        return moduleGroupName;
    }

    public void setModuleGroupName(String moduleGroupName) {
        this.moduleGroupName = moduleGroupName;
    }

    public String getFeaturePath() {
        return featurePath;
    }

    public void setFeaturePath(String featurePath) {
        this.featurePath = featurePath;
    }

    public String getFeatureName() {
        return featureName;
    }

    public void setFeatureName(String featureName) {
        this.featureName = featureName;
    }

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

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public Long getLoginAuditId() {
        return loginAuditId;
    }

    public void setLoginAuditId(Long loginAuditId) {
        this.loginAuditId = loginAuditId;
    }

    public Long getUsedCount() {
        return usedCount;
    }

    public void setUsedCount(Long usedCount) {
        this.usedCount = usedCount;
    }
}
