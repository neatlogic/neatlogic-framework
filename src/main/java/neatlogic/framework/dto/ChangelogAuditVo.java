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


    public ChangelogAuditVo() {
    }

    public ChangelogAuditVo(String tenantUuid, String moduleId, String sqlHash, String version) {
        this.tenantUuid = tenantUuid;
        this.moduleId = moduleId;
        this.version = version;
        this.sqlHash = sqlHash;
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
}
