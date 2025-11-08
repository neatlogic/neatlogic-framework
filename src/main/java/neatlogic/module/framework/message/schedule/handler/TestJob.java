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

package neatlogic.module.framework.message.schedule.handler;

import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.common.config.Config;
import neatlogic.framework.scheduler.core.JobBase;
import neatlogic.framework.scheduler.dto.JobObject;
import org.quartz.CronExpression;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.text.SimpleDateFormat;
import java.util.Date;

//@Service
@DisallowConcurrentExecution
public class TestJob extends JobBase {


    private final String cron = "* * * * * ?";
    private static Long currentTime = null;

    @Override
    public String getGroupName() {
        return TenantContext.get().getTenantUuid() + "-FUCK-BUG";
    }

    @Override
    public Boolean isMyHealthy(JobObject jobObject) {
        return true;
    }

    @Override
    public void reloadJob(JobObject jobObject) {
        if (CronExpression.isValidExpression(cron)) {
            JobObject.Builder newJobObjectBuilder = new JobObject.Builder(jobObject.getJobName(), this.getGroupName(), this.getClassName(), TenantContext.get().getTenantUuid()).withCron(cron);
            JobObject newJobObject = newJobObjectBuilder.build();
            schedulerManager.loadJob(newJobObject);
        }
    }

    @Override
    public void initJob(String tenantUuid) {
        JobObject.Builder jobObjectBuilder = new JobObject.Builder(this.getGroupName(), this.getGroupName(), this.getClassName(), TenantContext.get().getTenantUuid());
        JobObject jobObject = jobObjectBuilder.build();
        this.reloadJob(jobObject);
    }

    @Override
    public void executeInternal(JobExecutionContext context, JobObject jobObject) throws JobExecutionException, InterruptedException {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        long diff = 0;
        Thread.sleep(10000L);
        if (currentTime == null) {
            currentTime = System.currentTimeMillis();
        } else {
            diff = System.currentTimeMillis() - currentTime;
            currentTime = System.currentTimeMillis();
        }
        System.out.println("JOB1#" + Config.SCHEDULE_SERVER_ID + " gap:" + diff + "ms " + "now:" + sdf.format(new Date()) + " next:" + sdf.format(context.getNextFireTime()));
    }

}
