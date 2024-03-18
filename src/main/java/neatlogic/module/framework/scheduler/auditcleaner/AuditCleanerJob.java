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
