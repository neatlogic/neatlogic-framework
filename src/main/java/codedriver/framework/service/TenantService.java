package codedriver.framework.service;

import codedriver.framework.dto.TenantVo;

public interface TenantService {
	public TenantVo getTenantByUuid(String tenantUuid);
}
