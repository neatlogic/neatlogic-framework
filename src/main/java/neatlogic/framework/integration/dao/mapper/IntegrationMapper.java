/*
 *
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the NeatLogic Sustainable Use License (NSUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */

package neatlogic.framework.integration.dao.mapper;

import neatlogic.framework.common.dto.ValueTextVo;
import neatlogic.framework.integration.dto.IntegrationAuditVo;
import neatlogic.framework.integration.dto.IntegrationVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface IntegrationMapper {

    IntegrationAuditVo getIntegrationAuditById(Long id);

    List<IntegrationAuditVo> getIntegrationAuditListByIdList(List<Long> idList);

    List<IntegrationAuditVo> searchIntegrationAudit(IntegrationAuditVo integrationAuditVo);

    int getIntegrationAuditCount(IntegrationAuditVo integrationAuditVo);

    List<Long> getIntegrationAuditIdList(IntegrationAuditVo integrationAuditVo);

    IntegrationVo getIntegrationByUuid(String uuid);

    IntegrationVo getIntegrationByName(String name);

    int checkIntegrationExists(String uuid);

    List<IntegrationVo> searchIntegration(IntegrationVo integrationVo);

    List<IntegrationVo> getIntegrationListByUuidList(List<String> uuidList);

    List<ValueTextVo> searchIntegrationForSelect(IntegrationVo integrationVo);

    int searchIntegrationCount(IntegrationVo integrationVo);

    int checkNameIsRepeats(IntegrationVo integrationVo);

    List<String> checkUuidListExists(List<String> uuidList);

    List<Long> getNotIndexIntegrationAuditIdList(
            @Param("integrationUuid") String integrationUuid,
            @Param("id") Long startId,
            @Param("type") String type,
            @Param("pageSize") Integer pageSize
    );

    int insertIntegration(IntegrationVo integrationVo);

    int updateIntegration(IntegrationVo integrationVo);

    int updateIntegrationActive(IntegrationVo integrationVo);

    int deleteIntegrationByUuid(String uuid);

    int insertIntegrationAudit(IntegrationAuditVo integrationAuditVo);

    void deleteAuditByDayBefore(int dayBefore);
}
