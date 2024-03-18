/*Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.*/

package neatlogic.framework.integration.dao.mapper;

import neatlogic.framework.common.dto.ValueTextVo;
import neatlogic.framework.integration.dto.IntegrationAuditVo;
import neatlogic.framework.integration.dto.IntegrationVo;

import java.util.List;

public interface IntegrationMapper {

    List<IntegrationAuditVo> searchIntegrationAudit(IntegrationAuditVo integrationAuditVo);

    int getIntegrationAuditCount(IntegrationAuditVo integrationAuditVo);

    IntegrationVo getIntegrationByUuid(String uuid);

    IntegrationVo getIntegrationByName(String name);

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
