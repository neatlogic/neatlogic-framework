package codedriver.framework.dao.mapper;

import java.util.List;

import codedriver.framework.dto.ModuleVo;

public interface ModuleMapper {

	public List<ModuleVo> getAllModuleList();
	
	public List<ModuleVo> getActiveModuleListByTenantUuid(String tenantUuid);

}
