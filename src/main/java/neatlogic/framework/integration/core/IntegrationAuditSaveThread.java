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
