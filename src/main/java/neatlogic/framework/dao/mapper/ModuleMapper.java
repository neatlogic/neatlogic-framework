package neatlogic.framework.dao.mapper;

import neatlogic.framework.dao.aop.UseMasterDatabase;

import java.util.List;

@UseMasterDatabase
public interface ModuleMapper {

    List<String> getModuleGroupListByTenantUuid(String tenantUuid);

}
