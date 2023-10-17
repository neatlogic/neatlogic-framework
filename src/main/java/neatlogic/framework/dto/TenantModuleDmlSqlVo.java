package neatlogic.framework.dto;

import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.restful.annotation.EntityField;

public class TenantModuleDmlSqlVo {
    @EntityField(name = "租户uuid", type = ApiParamType.STRING)
    private String tenantUuid;

    @EntityField(name = "模块id", type = ApiParamType.STRING)
    private String moduleId;

    @EntityField(name = "sql哈希", type = ApiParamType.STRING)
    private String sqlMd5;

    @EntityField(name = "sql执行状态", type = ApiParamType.STRING)
    private Integer sqlStatus;

    @EntityField(name = "类型", type = ApiParamType.STRING)
    private String type;

    private String errorMsg;

    public TenantModuleDmlSqlVo() {
    }

    public TenantModuleDmlSqlVo(String tenantUuid, String moduleId, String sqlMd5, String type) {
        this.tenantUuid = tenantUuid;
        this.moduleId = moduleId;
        this.sqlMd5 = sqlMd5;
        this.sqlStatus = 1;
        this.type = type;
    }

    public TenantModuleDmlSqlVo(String tenantUuid, String moduleId, String sqlMd5, int sqlStatus, String errorMsg, String type) {
        this.tenantUuid = tenantUuid;
        this.moduleId = moduleId;
        this.sqlMd5 = sqlMd5;
        this.sqlStatus = sqlStatus;
        this.type = type;
        this.errorMsg = errorMsg;
    }

    public String getTenantUuid() {
        return tenantUuid;
    }

    public void setTenantUuid(String tenantUuid) {
        this.tenantUuid = tenantUuid;
    }

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    public String getSqlMd5() {
        return sqlMd5;
    }

    public void setSqlMd5(String sqlMd5) {
        this.sqlMd5 = sqlMd5;
    }

    public Integer getSqlStatus() {
        return sqlStatus;
    }

    public void setSqlStatus(Integer sqlStatus) {
        this.sqlStatus = sqlStatus;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
