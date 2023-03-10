package neatlogic.framework.dao.mapper;

import neatlogic.framework.dto.TenantAuditVo;
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

    List<String> getTenantModuleGroupByTenantId(Long tenantId);

    TenantVo getTenantByUuid(String tenantUuid);

    TenantVo getTenantById(Long tenantId);

    List<TenantVo> getAllActiveTenant();

    Long getTenantAuditMaxGroupIdByTenantId(Long tenantId);

    int insertTenant(TenantVo tenantVo);

    int insertTenantModuleGroup(@Param("tenantId") Long tenantId, @Param("tenantUuid") String tenantUuid, @Param("moduleGroup") String moduleGroup);

    int insertTenantAudit(TenantAuditVo tenantAuditVo);

    int insertTenantModule(TenantModuleVo tenantModuleVo);

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
