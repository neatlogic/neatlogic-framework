package codedriver.framework.dao.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import codedriver.framework.dto.TenantVo;

public interface TenantMapper {

	public TenantVo getTenantByUuid(String tenantUuid);

	public List<TenantVo> getAllActiveTenant();

	public int insertTenant(TenantVo tenantVo);

	public int insertTenantModule(@Param("tenantUuid") String tenantUuid, @Param("moduleId") String moduleId);

	public int updateTenant(TenantVo tenantVo);

	public int checkTenantUuidIsExists(TenantVo tenantVo);

	public int deleteTenantModuleByTenantUuid(String tenantUuid);
}
