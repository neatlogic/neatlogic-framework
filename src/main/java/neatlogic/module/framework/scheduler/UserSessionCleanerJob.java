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
                .withCron("0,30 * * * *")
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
