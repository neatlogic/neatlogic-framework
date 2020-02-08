package codedriver.framework.scheduler.core;

import java.util.List;

import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.scheduler.dto.JobClassVo;
import codedriver.framework.scheduler.dto.JobObject;
import codedriver.framework.scheduler.dto.JobVo;

public abstract class PublicJobBase extends JobBase implements IPublicJob {

	public Boolean checkCronIsExpired(JobObject jobObject) {
		JobVo jobVo = schedulerMapper.getJobBaseInfoByUuid(jobObject.getJobName());
		if (jobVo != null) {
			if (jobVo.getIsActive().equals(1) && jobVo.getCron().equals(jobObject.getCron())) {
				return true;
			}
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
			JobClassVo jobClassVo = SchedulerManager.getJobClassByClasspath(jobObject.getJobClassName());
			JobObject newJobObject = new JobObject.Builder(jobVo.getUuid(), this.getGroupName(), jobClassVo.getClassName(), tenantUuid).withCron(jobVo.getCron()).withBeginTime(jobVo.getBeginTime()).withEndTime(jobVo.getEndTime()).needAudit(jobVo.getNeedAudit()).setType("public").build();
			schedulerManager.loadJob(newJobObject);
		} else {
			schedulerManager.unloadJob(jobObject);
		}
	}

	@Override
	public void initJob(String tenantUuid) {
		List<JobVo> jobVoList = schedulerMapper.getJobByClassName(this.getClassName());
		JobClassVo jobClassVo = SchedulerManager.getJobClassByClasspath(this.getClassName());
		for (JobVo jobVo : jobVoList) {
			if (jobVo.getIsActive().equals(1)) {
				JobObject jobObject = new JobObject.Builder(jobVo.getUuid(), this.getGroupName(), this.getClassName(), tenantUuid).withCron(jobVo.getCron()).withBeginTime(jobVo.getBeginTime()).withEndTime(jobVo.getEndTime()).needAudit(jobVo.getNeedAudit()).setType("public").build();
				schedulerManager.loadJob(jobObject);
			}
		}
	}

	@Override
	public String getGroupName() {
		return TenantContext.get().getTenantUuid() + "-PUBLICJOB";
	}
}
