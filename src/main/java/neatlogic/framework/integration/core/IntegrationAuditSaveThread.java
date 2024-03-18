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
