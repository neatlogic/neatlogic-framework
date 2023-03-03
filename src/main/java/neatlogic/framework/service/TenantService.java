package neatlogic.framework.service;

import neatlogic.framework.dto.TenantVo;

public interface TenantService {
	public TenantVo getTenantByUuid(String tenantUuid);
}
