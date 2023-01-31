/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.module.framework.auditconfig.handler;

import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.auditconfig.core.AuditCleanerBase;
import neatlogic.framework.healthcheck.dao.mapper.DatabaseFragmentMapper;
import neatlogic.framework.scheduler.dao.mapper.SchedulerMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class ScheduleAuditCleaner extends AuditCleanerBase {
    @Resource
    private SchedulerMapper schedulerMapper;
    @Resource
    private DatabaseFragmentMapper databaseFragmentMapper;

    @Override
    public String getName() {
        return "SCHEDULER-AUDIT";
    }

    @Override
    protected void myClean(int dayBefore) {
        schedulerMapper.deleteAuditByDayBefore(dayBefore);
        databaseFragmentMapper.rebuildTable(TenantContext.get().getDbName(), "schedule_job_audit");

    }
}
