/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.integration.dao.mapper;

import codedriver.framework.common.dto.ValueTextVo;
import codedriver.framework.integration.dto.IntegrationAuditVo;
import codedriver.framework.integration.dto.IntegrationVo;

import java.util.List;

public interface IntegrationMapper {

    List<IntegrationAuditVo> searchIntegrationAudit(IntegrationAuditVo integrationAuditVo);

    int getIntegrationAuditCount(IntegrationAuditVo integrationAuditVo);

    IntegrationVo getIntegrationByUuid(String uuid);

    int checkIntegrationExists(String uuid);

    List<IntegrationVo> searchIntegration(IntegrationVo integrationVo);

    List<IntegrationVo> getIntegrationListByUuidList(List<String> uuidList);

    List<ValueTextVo> searchIntegrationForSelect(IntegrationVo integrationVo);

    int searchIntegrationCount(IntegrationVo integrationVo);

    int checkNameIsRepeats(IntegrationVo integrationVo);

    List<String> checkUuidListExists(List<String> uuidList);

    int insertIntegration(IntegrationVo integrationVo);

    int updateIntegration(IntegrationVo integrationVo);

    int updateIntegrationActive(IntegrationVo integrationVo);

    int deleteIntegrationByUuid(String uuid);

    int insertIntegrationAudit(IntegrationAuditVo integrationAuditVo);

    void deleteAuditByDayBefore(int dayBefore);
}
