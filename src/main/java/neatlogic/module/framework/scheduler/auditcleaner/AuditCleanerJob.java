/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.module.framework.scheduler.auditcleaner;

import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.auditconfig.core.AuditCleanerFactory;
import neatlogic.framework.auditconfig.core.IAuditCleaner;
import neatlogic.framework.auditconfig.dao.mapper.AuditConfigMapper;
import neatlogic.framework.auditconfig.dto.AuditConfigVo;
import neatlogic.framework.scheduler.core.JobBase;
import neatlogic.framework.scheduler.dto.JobObject;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 清理审计日志作业
 */
@Component
@DisallowConcurrentExecution
public class AuditCleanerJob extends JobBase {

    @Resource
    private AuditConfigMapper auditConfigMapper;

    @Override
    public String getGroupName() {
        return TenantContext.get().getTenantUuid() + "-AUDIT-CLEANER-GROUP";
    }

    @Override
    public Boolean isMyHealthy(JobObject jobObject) {
        return true;
    }

    @Override
    public void reloadJob(JobObject jobObject) {
        schedulerManager.loadJob(jobObject);
    }

    @Override
    public void initJob(String tenantUuid) {
        //每天凌晨2点运行
        JobObject jobObject = new JobObject.Builder("AUDIT-CLEANER-JOB", this.getGroupName(), this.getClassName(), tenantUuid)
                .withCron("0 0 2 * * ?")
                //.withCron("0 * * * * ?")//测试用
                .build();
        this.reloadJob(jobObject);
    }

    @Override
    public void executeInternal(JobExecutionContext context, JobObject jobObject) throws Exception {
        List<AuditConfigVo> auditConfigList = auditConfigMapper.searchAuditConfig();
        for (AuditConfigVo auditConfigVo : auditConfigList) {
            IAuditCleaner cleaner = AuditCleanerFactory.getCleaner(auditConfigVo.getName());
            if (cleaner != null) {
                cleaner.clean();
            }
        }
    }
}
