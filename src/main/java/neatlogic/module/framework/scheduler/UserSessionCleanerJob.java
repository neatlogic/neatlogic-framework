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
    @Override
    public String getName() {
        return "用户会话定时清理";
    }

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
