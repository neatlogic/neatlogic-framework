package codedriver.framework.integration.dao.mapper;

import java.util.List;

import codedriver.framework.common.dto.ValueTextVo;
import codedriver.framework.integration.dto.IntegrationVo;

public interface IntegrationMapper {
	public IntegrationVo getIntegrationByUuid(String uuid);

	public List<IntegrationVo> searchIntegration(IntegrationVo integrationVo);

	public int searchIntegrationCount(IntegrationVo integrationVo);

	public List<ValueTextVo> searchActiveIntegrationForSelect(IntegrationVo integrationVo);

	public int insertIntegration(IntegrationVo integrationVo);

	public int updateIntegration(IntegrationVo integrationVo);
}
