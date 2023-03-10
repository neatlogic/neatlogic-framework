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

package neatlogic.framework.scheduler.core;

import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.scheduler.dto.JobClassVo;
import neatlogic.framework.scheduler.dto.JobObject;
import neatlogic.framework.scheduler.dto.JobVo;

import java.util.List;

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
        JobVo jobVo = schedulerMapper.getJobBaseInfoByUuid(jobObject.getJobName());
        if (jobVo != null) {
			return jobVo.getIsActive().equals(1) && jobVo.getCron().equals(jobObject.getCron());
        }
        return false;
    }

	@Override
	public void reloadJob(JobObject jobObject) {
		String tenantUuid = jobObject.getTenantUuid();
		// 切换租户库
		TenantContext.get().switchTenant(tenantUuid);
		JobVo jobVo = schedulerMapper.getJobBaseInfoByUuid(jobObject.getJobName());
		if (jobVo != null && jobVo.getIsActive().equals(1)) {
			JobClassVo jobClassVo = SchedulerManager.getJobClassByClassName(jobObject.getJobHandler());
			JobObject newJobObject = new JobObject.Builder(jobVo.getUuid(), this.getGroupName(), jobClassVo.getClassName(), tenantUuid).withCron(jobVo.getCron()).withBeginTime(jobVo.getBeginTime()).withEndTime(jobVo.getEndTime()).needAudit(jobVo.getNeedAudit()).setType("public").build();
			schedulerManager.loadJob(newJobObject);
		} else {
			schedulerManager.unloadJob(jobObject);
		}
	}

	@Override
	public void initJob(String tenantUuid) {
		List<JobVo> jobVoList = schedulerMapper.getJobByHandler(this.getClassName());
		for (JobVo jobVo : jobVoList) {
			if (jobVo.getIsActive().equals(1)) {
				JobObject jobObject = new JobObject.Builder(jobVo.getUuid(), this.getGroupName(), this.getClassName(), tenantUuid).withCron(jobVo.getCron()).withBeginTime(jobVo.getBeginTime()).withEndTime(jobVo.getEndTime()).needAudit(jobVo.getNeedAudit()).setType("public").build();
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
}
