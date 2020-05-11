package codedriver.framework.integration.dao.mapper;

import java.util.List;

import codedriver.framework.integration.dto.IntegrationInvokeVo;
import codedriver.framework.integration.dto.IntegrationVo;

public interface IntegrationMapper {
	public IntegrationVo getIntegrationByUuid(String uuid);

	public List<IntegrationVo> searchIntegration(IntegrationVo integrationVo);

	public int searchIntegrationCount(IntegrationVo integrationVo);

	public int insertIntegration(IntegrationVo integrationVo);

	public int updateIntegration(IntegrationVo integrationVo);

	public int updateIntegrationActive(IntegrationVo integrationVo);

	public int replaceIntegrationInvoke(IntegrationInvokeVo integrationInvokeVo);

	public int deleteIntegrationByUuid(String uuid);
}
