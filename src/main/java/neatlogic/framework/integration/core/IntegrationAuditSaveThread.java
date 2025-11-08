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

package neatlogic.framework.integration.core;

import neatlogic.framework.asynchronization.thread.NeatLogicThread;
import neatlogic.framework.integration.dao.mapper.IntegrationMapper;
import neatlogic.framework.integration.dto.IntegrationAuditVo;
import neatlogic.framework.util.AuditUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Deprecated
public class IntegrationAuditSaveThread extends NeatLogicThread {

    private static IntegrationMapper integrationMapper;

    @Autowired
    public void setIntegrationMapper(IntegrationMapper _integrationMapper) {
        integrationMapper = _integrationMapper;
    }

    private IntegrationAuditVo integrationAuditVo;

    public IntegrationAuditSaveThread(IntegrationAuditVo _integrationAuditVo) {
        super("INTEGRATION-AUDIT-SAVER");
        integrationAuditVo = _integrationAuditVo;
    }

    public IntegrationAuditSaveThread() {
        super("INTEGRATION-AUDIT-SAVER");
    }

    @Override
    protected void execute() {
        if (integrationAuditVo != null) {
            AuditUtil.saveAuditDetail(integrationAuditVo, "integration_audit");
            integrationMapper.insertIntegrationAudit(integrationAuditVo);
        }
    }

}
