package neatlogic.framework.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import neatlogic.framework.dao.mapper.TenantMapper;
import neatlogic.framework.dto.TenantVo;

@Service
public class TenantServiceImpl implements TenantService {

	@Autowired
	private TenantMapper tenantMapper;

	@Override
	public TenantVo getTenantByUuid(String tenantUuid) {
		return tenantMapper.getTenantByUuid(tenantUuid);
	}

}
