package codedriver.framework.scheduler.service;

import java.util.List;

import codedriver.framework.scheduler.dto.JobAuditVo;
import codedriver.framework.scheduler.dto.JobClassVo;
import codedriver.framework.scheduler.dto.JobVo;

public interface SchedulerService {

	List<JobVo> searchJobList(JobVo jobVo);

	List<JobClassVo> searchJobClassList(JobClassVo jobClassVo);

	List<JobAuditVo> searchJobAuditList(JobAuditVo jobAuditVo);

	int saveJob(JobVo job);

	int saveJobClass(JobClassVo jobClassVo);

	int getJobLock(Long jobId, int scheduleServerId);
	
	public void loadJob(JobVo job);
	
	public void stopJob(Long jobId);
	
	public void deleteJob(Long jobId);
	
}
