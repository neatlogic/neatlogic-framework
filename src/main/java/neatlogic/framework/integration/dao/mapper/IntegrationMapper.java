/*
 * Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package neatlogic.framework.integration.dao.mapper;

import neatlogic.framework.common.dto.ValueTextVo;
import neatlogic.framework.integration.dto.IntegrationAuditVo;
import neatlogic.framework.integration.dto.IntegrationVo;

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
