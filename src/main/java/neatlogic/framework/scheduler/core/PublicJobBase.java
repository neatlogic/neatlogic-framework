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

package neatlogic.framework.scheduler.core;

import com.alibaba.fastjson.JSON;
import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.crossover.CrossoverServiceFactory;
import neatlogic.framework.crossover.IServerCrossoverService;
import neatlogic.framework.scheduler.dto.JobClassVo;
import neatlogic.framework.scheduler.dto.JobObject;
import neatlogic.framework.scheduler.dto.JobVo;

import java.util.List;
import java.util.Objects;

/**
 * 1、所有外部作业必须继承此父类
 * 2、外部作业默认不审计
 * 3、外部作业的相关配置（cron、是否审计、计划开始时间等）从 `schedule_job` 获取；
 * 4、`schedule_job` 表仅供 自定义外部作业使用(结合”定时作业“页面修改)
 * 5、job_name 是外部作业的uuid，即`schedule_job`的对应`uuid`字段
 * 6、job_group 只能是 “租户-PUBLICJOB“ 不允许自定义
 */
public abstract class PublicJobBase extends JobBase implements IPublicJob {

    @Override
    public Boolean isMyHealthy(JobObject jobObject) {
        if (jobObject.isTest() == 1) {
            return true;
        }
        JobVo jobVo = schedulerMapper.getJobBaseInfoByUuid(jobObject.getJobName());
        if (jobVo != null) {
            return jobVo.getIsActive().equals(1) && Objects.equals(jobVo.getCron(), jobObject.getCron());
        }
        return false;
    }

    @Override
    public void reloadJob(JobObject jobObject) {
        String tenantUuid = jobObject.getTenantUuid();
        // 切换租户库
        TenantContext.get().switchTenant(tenantUuid);
        JobVo jobVo = schedulerMapper.getJobByUuid(jobObject.getJobName());
        if (jobVo != null && jobVo.getIsActive().equals(1)) {
            JobClassVo jobClassVo = SchedulerManager.getJobClassByClassName(jobObject.getJobHandler());
            JobObject newJobObject = new JobObject.Builder(jobVo.getUuid(), this.getGroupName(), jobClassVo.getClassName(), tenantUuid)
                    .withCron(jobVo.getCron())
                    .withBeginTime(jobVo.getBeginTime())
                    .withEndTime(jobVo.getEndTime())
                    .needAudit(jobVo.getNeedAudit())
                    .withPropList(jobVo.getPropList())
                    .setType("public")
                    .build();
            schedulerManager.loadJob(newJobObject);
        } else {
            schedulerManager.unloadJob(jobObject);
        }
    }

    @Override
    public void initJob(String tenantUuid) {
        IServerCrossoverService serverCrossoverService = CrossoverServiceFactory.getApi(IServerCrossoverService.class);
        List<Integer> currentGroupServerIdList = serverCrossoverService.getCurrentGroupServerIdList();
        List<JobVo> jobVoList = schedulerMapper.getJobByHandler(this.getClassName());
        for (JobVo jobVo : jobVoList) {
            if (jobVo.getSourceServerId() != null && !currentGroupServerIdList.contains(jobVo.getSourceServerId())) {
                System.out.println("jobVo = " + JSON.toJSON(jobVo));
                continue;
            }
            if (jobVo.getIsActive().equals(1)) {
                JobObject jobObject = new JobObject.Builder(jobVo.getUuid(), this.getGroupName(), this.getClassName(), tenantUuid)
                        .withCron(jobVo.getCron())
                        .withBeginTime(jobVo.getBeginTime())
                        .withEndTime(jobVo.getEndTime())
                        .needAudit(jobVo.getNeedAudit())
                        .setType("public")
                        .withPropList(jobVo.getPropList())
                        .build();
                schedulerManager.loadJob(jobObject);
            }
        }
    }

    @Override
    public final String getGroupName() {
        return TenantContext.get().getTenantUuid() + "-PUBLICJOB";
    }

    @Override
    public final Boolean isAudit() {
        return false;//外部作业默认不启用，如需启用，需到页面配置
    }

    @Override
    public JobVo getJob(String uuid) {
        return schedulerMapper.getJobByUuid(uuid);
    }

    @Override
    public String getType() {
        return "public";
    }
}
