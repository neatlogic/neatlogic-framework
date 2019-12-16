package codedriver.framework.dao.mapper;

import java.util.List;

import codedriver.framework.dto.TenantVo;

public interface TenantMapper {

	public TenantVo getTenantByUuid(String tenantUuid);

	public List<TenantVo> getAllActiveTenant();
}
