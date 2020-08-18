package codedriver.framework.integration.dao.mapper;

import java.util.List;

import codedriver.framework.common.dto.ValueTextVo;
import org.apache.ibatis.annotations.Param;

import codedriver.framework.integration.dto.IntegrationAuditVo;
import codedriver.framework.integration.dto.IntegrationInvokeVo;
import codedriver.framework.integration.dto.IntegrationVo;

public interface IntegrationMapper {

	public List<IntegrationAuditVo> searchIntegrationAudit(IntegrationAuditVo integrationAuditVo);

	public int searchIntegrationAuditCount(IntegrationAuditVo integrationAuditVo);

	public IntegrationVo getIntegrationByUuid(String uuid);

	public List<IntegrationVo> searchIntegration(IntegrationVo integrationVo);

	public List<ValueTextVo> searchIntegrationForSelect(IntegrationVo integrationVo);

	public int searchIntegrationCount(IntegrationVo integrationVo);

	public int insertIntegration(IntegrationVo integrationVo);

	public int updateIntegration(IntegrationVo integrationVo);

	public int updateIntegrationActive(IntegrationVo integrationVo);

	public int replaceIntegrationInvoke(IntegrationInvokeVo integrationInvokeVo);

	public int deleteIntegrationByUuid(String uuid);

	public int insertIntegrationAudit(IntegrationAuditVo integrationAuditVo);

}
