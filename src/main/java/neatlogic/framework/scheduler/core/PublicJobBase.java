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
		List<JobVo> jobVoList = schedulerMapper.getJobByHandler(this.getClassName());
		for (JobVo jobVo : jobVoList) {
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
}
