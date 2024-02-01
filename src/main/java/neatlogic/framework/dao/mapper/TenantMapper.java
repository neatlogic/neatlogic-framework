package neatlogic.framework.dao.mapper;

import neatlogic.framework.dto.TenantAuditVo;
import neatlogic.framework.dto.TenantModuleGroupVo;
import neatlogic.framework.dto.TenantModuleVo;
import neatlogic.framework.dto.TenantVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TenantMapper {

    String getTenantAuditDetailByHash(String hash);

    List<TenantAuditVo> searchTenantAudit(TenantAuditVo tenantAuditVo);

    int searchTenantAuditCount(TenantAuditVo tenantAuditVo);

    int searchTenantCount(TenantVo tenantVo);

    List<TenantVo> searchTenant(TenantVo tenantVo);

    List<TenantModuleGroupVo> getTenantModuleGroupByTenantUuid(String tenantUuid);

    List<TenantModuleVo> getTenantModuleByTenantUuid(String tenantUuid);

    TenantVo getTenantByUuid(String tenantUuid);

    List<TenantVo> getAllActiveTenant();

    Long getTenantAuditMaxGroupIdByTenantUuid(String tenantUuid);

    List<String> getTenantModuleDmlSqlMd5ByTenantUuidAndModuleId(@Param("tenantUuid") String tenantUuid, @Param("moduleId") String moduleId);

    List<TenantAuditVo> getTenantAuditListWithDmlDemo(String tenantUuid);

    int insertTenant(TenantVo tenantVo);

    int insertTenantModuleGroup( @Param("tenantUuid") String tenantUuid, @Param("moduleGroup") String moduleGroup, @Param("isInitDml") Boolean isInitDml);

    int insertTenantAudit(TenantAuditVo tenantAuditVo);

    int insertTenantModule(@Param("tenantModule") TenantModuleVo tenantModuleVo, @Param("updateTag") Long updateTag);

    int insertTenantModuleDmlSql(@Param("tenantUuid") String tenantUuid, @Param("moduleId") String moduleId, @Param("currentRunSqlMd5List") List<String> currentRunSqlMd5List,@Param("sqlStatus")int sqlStatus);

    int replaceTenantAuditDetail(@Param("hash") String hash, @Param("content") String content);

    int updateTenantByUuid(TenantVo tenantVo);

    int updateTenantIsActiveByUuid(String uuid);

    int updateTenantAudit(TenantAuditVo tenantAuditVo);

    int updateTenantStatusByUuid(@Param("uuid") String uuid,@Param("status") String status);

    int updateTenantModule(TenantModuleVo tenantModuleVo);

    int updateTenantVisitTime(String tenant);

    int checkTenantUuidIsExists(@Param("uuid") String uuid);

    int deleteTenantModuleGroupByTenantUuid(String tenantUuid);

    void deleteTenantModuleByTenantUuid(String tenantUuid);

}
