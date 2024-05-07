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

package neatlogic.module.framework.scheduler;

import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.common.config.Config;
import neatlogic.framework.dao.mapper.UserSessionMapper;
import neatlogic.framework.scheduler.core.JobBase;
import neatlogic.framework.scheduler.dto.JobObject;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 清理用户登录会话作业
 */
@Component
@DisallowConcurrentExecution
public class UserSessionCleanerJob extends JobBase {

    @Resource
    private UserSessionMapper userSessionMapper;

    @Override
    public String getGroupName() {
        return TenantContext.get().getTenantUuid() + "-USER-SESSION-CLEANER-GROUP";
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
        //每半个小时运行
        JobObject jobObject = new JobObject.Builder("USER-SESSION-CLEANER-JOB", this.getGroupName(), this.getClassName(), tenantUuid)
                .withCron("0 0,30 * * * ?")
                //.withIntervalInSeconds(3)//test
                .build();
        this.reloadJob(jobObject);
    }

    @Override
    public void executeInternal(JobExecutionContext context, JobObject jobObject) throws Exception {
        int expire = Config.USER_EXPIRETIME();
        long expireTime = expire * 60L * 1000L;
        Date now = new Date();
        expireTime = now.getTime() - expireTime;
        userSessionMapper.deleteUserSessionByExpireTime(expireTime);
    }
}
