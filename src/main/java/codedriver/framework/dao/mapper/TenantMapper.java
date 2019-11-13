package codedriver.framework.dao.mapper;

import codedriver.framework.dto.TenantVo;

public interface TenantMapper {

	public TenantVo getTenantByUuid(String tenantUuid);

}
