package codedriver.framework.dao.mapper;

import java.util.List;

public interface ModuleMapper {

	public List<String> getModuleListByTenantUuid(String tenantUuid);

}
