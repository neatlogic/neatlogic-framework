/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.framework.integration.core;

import neatlogic.framework.asynchronization.thread.CodeDriverThread;
import neatlogic.framework.integration.dao.mapper.IntegrationMapper;
import neatlogic.framework.integration.dto.IntegrationAuditVo;
import neatlogic.framework.util.AuditUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Deprecated
public class IntegrationAuditSaveThread extends CodeDriverThread {

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
