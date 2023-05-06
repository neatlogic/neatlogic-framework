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

    List<TenantModuleGroupVo> getTenantModuleGroupByTenantId(Long tenantId);

    List<TenantModuleVo> getTenantModuleByTenantId(Long tenantId);

    TenantVo getTenantByUuid(String tenantUuid);

    TenantVo getTenantById(Long tenantId);

    List<TenantVo> getAllActiveTenant();

    Long getTenantAuditMaxGroupIdByTenantId(Long tenantId);

    int insertTenant(TenantVo tenantVo);

    int insertTenantModuleGroup(@Param("tenantId") Long tenantId, @Param("tenantUuid") String tenantUuid, @Param("moduleGroup") String moduleGroup,@Param("isInitDml") Integer isInitDml);

    int insertTenantAudit(TenantAuditVo tenantAuditVo);

    int insertTenantModule(@Param("tenantModule") TenantModuleVo tenantModuleVo,@Param("updateTag") Long updateTag);

    int replaceTenantAuditDetail(@Param("hash") String hash, @Param("content") String content);

    int updateTenant(TenantVo tenantVo);

    int updateTenantIsActive(TenantVo tenantVo);

    int updateTenantAudit(TenantAuditVo tenantAuditVo);

    int updateTenantStatus(TenantVo tenantVo);

    int updateTenantModule(TenantModuleVo tenantModuleVo);

    int checkTenantUuidIsExists(TenantVo tenantVo);

    int deleteTenantModuleGroupByTenantId(Long tenantId);

    void deleteTenantModuleByTenantId(Long id);

}
