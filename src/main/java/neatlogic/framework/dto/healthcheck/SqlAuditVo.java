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

package neatlogic.framework.dto.healthcheck;

import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.common.dto.BasePageVo;
import neatlogic.framework.restful.annotation.EntityField;

import java.util.Date;

public class SqlAuditVo extends BasePageVo {
    @EntityField(name = "id", type = ApiParamType.STRING)
    private String id;
    @EntityField(name = "耗时(ms)", type = ApiParamType.LONG)
    private Long timeCost;
    @EntityField(name = "sql内容", type = ApiParamType.STRING)
    private String sql;
    @EntityField(name = "执行时间", type = ApiParamType.STRING)
    private Date runTime;
    @EntityField(name = "记录条数", type = ApiParamType.INTEGER)
    private int recordCount = 0;
    @EntityField(name = "租户", type = ApiParamType.STRING)
    private String tenant;
    @EntityField(name = "用户", type = ApiParamType.STRING)
    private String userId;
    @EntityField(name = "使用到的缓存级别", type = ApiParamType.STRING)
    private String useCacheLevel;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTenant() {
        return tenant;
    }

    public void setTenant(String tenant) {
        this.tenant = tenant;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Long getTimeCost() {
        return timeCost;
    }

    public void setTimeCost(Long timeCost) {
        this.timeCost = timeCost;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public Date getRunTime() {
        return runTime;
    }

    public void setRunTime(Date runTime) {
        this.runTime = runTime;
    }

    public int getRecordCount() {
        return recordCount;
    }

    public void setRecordCount(int recordCount) {
        this.recordCount = recordCount;
    }

    public String getUseCacheLevel() {
        return useCacheLevel;
    }

    public void setUseCacheLevel(String useCacheLevel) {
        this.useCacheLevel = useCacheLevel;
    }
}
