package codedriver.framework.dao.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import codedriver.framework.dto.TenantAuditVo;
import codedriver.framework.dto.TenantVo;

public interface TenantMapper {

	public int searchTenantCount(TenantVo tenantVo);

	public List<TenantVo> searchTenant(TenantVo tenantVo);

	public TenantVo getTenantByUuid(String tenantUuid);

	public List<TenantVo> getAllActiveTenant();

	public int insertTenant(TenantVo tenantVo);

	public int insertTenantModule(@Param("tenantUuid") String tenantUuid, @Param("moduleId") String moduleId);

	public int insertTenantAudit(TenantAuditVo tenantAuditVo);

	public int updateTenant(TenantVo tenantVo);

	public int updateTenantAudit(TenantAuditVo tenantAuditVo);

	public int checkTenantUuidIsExists(TenantVo tenantVo);

	public int deleteTenantModuleByTenantUuid(String tenantUuid);
}
