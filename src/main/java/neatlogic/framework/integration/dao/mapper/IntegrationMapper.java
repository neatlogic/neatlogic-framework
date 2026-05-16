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
import neatlogic.framework.dto.AuthorityVo;
import neatlogic.framework.integration.dto.IntegrationAuditVo;
import neatlogic.framework.integration.dto.IntegrationAuthorityVo;
import neatlogic.framework.integration.dto.IntegrationRateLimitVo;
import neatlogic.framework.integration.dto.IntegrationVo;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
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

    List<IntegrationVo> listActiveIntegration();

    List<IntegrationVo> getIntegrationListByUuidList(List<String> uuidList);

    List<ValueTextVo> searchIntegrationForSelect(IntegrationVo integrationVo);

    int searchIntegrationCount(IntegrationVo integrationVo);

    int checkUserHasIntegrationAuthority(@Param("integrationUuid") String integrationUuid,
                                         @Param("action") String action,
                                         @Param("userUuid") String userUuid,
                                         @Param("teamUuidList") List<String> teamUuidList,
                                         @Param("roleUuidList") List<String> roleUuidList);

    List<IntegrationAuthorityVo> getIntegrationAuthorityListByIntegrationUuidListAndAction(@Param("integrationUuidList") List<String> integrationUuidList, @Param("action") String action);

    List<AuthorityVo> getIntegrationAuthorityListByIntegrationUuidAndAction(@Param("integrationUuid") String integrationUuid, @Param("action") String action);

    int checkNameIsRepeats(IntegrationVo integrationVo);

    List<String> checkUuidListExists(List<String> uuidList);

    List<Long> getNotIndexIntegrationAuditIdList(
            @Param("integrationUuid") String integrationUuid,
            @Param("id") Long startId,
            @Param("type") String type,
            @Param("pageSize") Integer pageSize
    );

    int insertIntegration(IntegrationVo integrationVo);

    int insertIntegrationAuthority(@Param("integrationUuid") String integrationUuid, @Param("authorityVo") AuthorityVo authorityVo);

    int updateIntegration(IntegrationVo integrationVo);

    int updateIntegrationActive(IntegrationVo integrationVo);

    int deleteIntegrationByUuid(String uuid);

    int insertIntegrationRateLimitIfAbsent(String integrationUuid);

    IntegrationRateLimitVo getIntegrationRateLimitForUpdate(String integrationUuid);

    Date getDatabaseCurrentTime();

    int resetIntegrationRateLimit(@Param("integrationUuid") String integrationUuid, @Param("windowStartTime") Date windowStartTime);

    int increaseIntegrationRateLimitCounter(String integrationUuid);

    int deleteIntegrationRateLimitByIntegrationUuid(String integrationUuid);

    int deleteIntegrationAuthorityByIntegrationUuidAndAction(@Param("integrationUuid") String integrationUuid, @Param("action") String action);

    int insertIntegrationAudit(IntegrationAuditVo integrationAuditVo);

    void deleteAuditByDayBefore(int dayBefore);
}
