package codedriver.framework.dao.mapper;

import java.util.List;

import codedriver.framework.dto.DatasourceVo;

public interface DatasourceMapper {
	public List<DatasourceVo> getAllActiveTenantDatasource();

	public int insertDatasource(DatasourceVo datasourceVo);

	public int createDatasource(String tenantUuid);
}
