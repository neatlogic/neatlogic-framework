package neatlogic.framework.dto;

import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.restful.annotation.EntityField;

public class ChangelogAuditVo {
    @EntityField(name = "租户uuid", type = ApiParamType.STRING)
    private String tenantUuid;

    @EntityField(name = "模块id", type = ApiParamType.STRING)
    private String moduleId;

    @EntityField(name = "版本", type = ApiParamType.STRING)
    private String version;

    @EntityField(name = "sql哈希", type = ApiParamType.STRING)
    private String sqlHash;

    @EntityField(name = "sql执行状态", type = ApiParamType.INTEGER)
    private Integer sqlStatus;

    @EntityField(name = "是否已忽略", type = ApiParamType.INTEGER)
    private Integer ignored = 0;

    @EntityField(name = "异常信息", type = ApiParamType.STRING)
    private String errorMsg;


    public ChangelogAuditVo() {
    }

    public ChangelogAuditVo(String tenantUuid, String moduleId, String sqlHash, String version,String errorMsg,Integer sqlStatus,Integer ignored) {
        this.tenantUuid = tenantUuid;
        this.moduleId = moduleId;
        this.version = version;
        this.sqlHash = sqlHash;
        this.errorMsg = errorMsg;
        this.sqlStatus = sqlStatus;
        this.ignored = ignored;
    }

    public ChangelogAuditVo(String tenantUuid, String moduleId, String sqlHash, String version,Integer sqlStatus) {
        this.tenantUuid = tenantUuid;
        this.moduleId = moduleId;
        this.version = version;
        this.sqlHash = sqlHash;
        this.sqlStatus = sqlStatus;
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

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getSqlHash() {
        return sqlHash;
    }

    public void setSqlHash(String sqlHash) {
        this.sqlHash = sqlHash;
    }

    public Integer getSqlStatus() {
        return sqlStatus;
    }

    public void setSqlStatus(Integer sqlStatus) {
        this.sqlStatus = sqlStatus;
    }

    public Integer getIgnored() {
        return ignored;
    }

    public void setIgnored(Integer ignored) {
        this.ignored = ignored;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
