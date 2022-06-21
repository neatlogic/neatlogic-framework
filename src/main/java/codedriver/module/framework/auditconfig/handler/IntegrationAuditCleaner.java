/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.module.framework.auditconfig.handler;

import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.auditconfig.core.AuditCleanerBase;
import codedriver.framework.healthcheck.dao.mapper.DatabaseFragmentMapper;
import codedriver.framework.integration.dao.mapper.IntegrationMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class IntegrationAuditCleaner extends AuditCleanerBase {
    @Resource
    private IntegrationMapper integrationMapper;
    @Resource
    private DatabaseFragmentMapper databaseFragmentMapper;

    @Override
    public String getName() {
        return "INTEGRATION-AUDIT";
    }

    @Override
    protected void myClean(int dayBefore) {
        integrationMapper.deleteAuditByDayBefore(dayBefore);
        databaseFragmentMapper.rebuildTable(TenantContext.get().getDbName(), "integration_audit");
    }
}
