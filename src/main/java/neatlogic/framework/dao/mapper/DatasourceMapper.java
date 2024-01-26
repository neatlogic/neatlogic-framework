package neatlogic.framework.dao.mapper;

import neatlogic.framework.dto.DatasourceVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DatasourceMapper {
	List<DatasourceVo> getAllActiveTenantDatasource();

	List<DatasourceVo> getAllTenantDatasource();

	DatasourceVo getDatasourceByTenantUuid(String tenantUuid);

	List<String> getUserGrants(String tenantUuid);

	int updateDatabaseUserPwd(@Param("tenantUuid") String tenantUuid, @Param("password") String password);

	int insertDatasource(DatasourceVo datasourceVo);

	int createDatabaseUser(DatasourceVo datasourceVo);

	int createDatasource(DatasourceVo datasourceVo);

}
