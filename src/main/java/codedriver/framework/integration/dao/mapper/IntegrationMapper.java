package codedriver.framework.integration.dao.mapper;

import codedriver.framework.common.dto.ValueTextVo;
import codedriver.framework.integration.dto.IntegrationAuditVo;
import codedriver.framework.integration.dto.IntegrationInvokeVo;
import codedriver.framework.integration.dto.IntegrationVo;

import java.util.List;

public interface IntegrationMapper {

    List<IntegrationAuditVo> searchIntegrationAudit(IntegrationAuditVo integrationAuditVo);

    int searchIntegrationAuditCount(IntegrationAuditVo integrationAuditVo);

    IntegrationVo getIntegrationByUuid(String uuid);

    int checkIntegrationExists(String uuid);

    List<IntegrationVo> searchIntegration(IntegrationVo integrationVo);

    List<ValueTextVo> searchIntegrationForSelect(IntegrationVo integrationVo);

    int searchIntegrationCount(IntegrationVo integrationVo);

    int checkNameIsRepeats(IntegrationVo integrationVo);

    List<String> checkUuidListExists(List<String> uuidList);

    int insertIntegration(IntegrationVo integrationVo);

    int updateIntegration(IntegrationVo integrationVo);

    int updateIntegrationActive(IntegrationVo integrationVo);

    int replaceIntegrationInvoke(IntegrationInvokeVo integrationInvokeVo);

    int deleteIntegrationByUuid(String uuid);

    int insertIntegrationAudit(IntegrationAuditVo integrationAuditVo);

}
