package neatlogic.framework.service;

import neatlogic.framework.dao.mapper.TenantMapper;
import neatlogic.framework.dto.TenantVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class TenantServiceImpl implements TenantService {

	@Resource
	private TenantMapper tenantMapper;

	@Override
	public TenantVo getTenantByUuid(String tenantUuid) {
		return tenantMapper.getTenantByUuid(tenantUuid);
	}

}
