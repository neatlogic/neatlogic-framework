package neatlogic.framework.dao.mapper;

import neatlogic.framework.dto.DatasourceVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DatasourceMapper {
	List<DatasourceVo> getAllActiveTenantDatasource();

	List<DatasourceVo> getAllTenantDatasource();

	DatasourceVo getDatasourceByTenantUuid(String tenantUuid);

	int updateDatasourcePasswordByTenantId(@Param("tenantId") Long tenantId, @Param("password") String password);

	int insertDatasource(DatasourceVo datasourceVo);

	int createDatasource(DatasourceVo datasourceVo);

}
