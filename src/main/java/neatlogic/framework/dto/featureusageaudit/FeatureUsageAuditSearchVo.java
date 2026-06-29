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

import neatlogic.framework.common.dto.BasePageVo;

import java.io.Serial;
import java.util.Date;
import java.util.List;

public class FeatureUsageAuditSearchVo extends BasePageVo {
    @Serial
    private static final long serialVersionUID = 5271912550863395608L;

    private List<String> moduleGroupList;
    private String userUuid;
    private List<String> featureNameList;
    private Date startTime;
    private Date endTime;
    private Long loginAuditId;

    public List<String> getModuleGroupList() {
        return moduleGroupList;
    }

    public void setModuleGroupList(List<String> moduleGroupList) {
        this.moduleGroupList = moduleGroupList;
    }

    public String getUserUuid() {
        return userUuid;
    }

    public void setUserUuid(String userUuid) {
        this.userUuid = userUuid;
    }

    public List<String> getFeatureNameList() {
        return featureNameList;
    }

    public void setFeatureNameList(List<String> featureNameList) {
        this.featureNameList = featureNameList;
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

    public Long getLoginAuditId() {
        return loginAuditId;
    }

    public void setLoginAuditId(Long loginAuditId) {
        this.loginAuditId = loginAuditId;
    }
}
