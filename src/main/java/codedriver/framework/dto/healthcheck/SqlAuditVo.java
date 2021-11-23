/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.dto.healthcheck;

import codedriver.framework.common.constvalue.ApiParamType;
import codedriver.framework.common.dto.BasePageVo;
import codedriver.framework.restful.annotation.EntityField;

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
}
