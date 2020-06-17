package codedriver.framework.scheduler.service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import codedriver.framework.scheduler.dto.JobAuditVo;
import codedriver.framework.scheduler.dto.JobClassVo;
import codedriver.framework.scheduler.dto.JobLockVo;
import codedriver.framework.scheduler.dto.JobVo;

public interface SchedulerService {

	public List<JobVo> searchJobList(JobVo jobVo);

	public List<JobClassVo> searchJobClassList(JobClassVo jobClassVo);

	public List<JobAuditVo> searchJobAudit(JobAuditVo jobAuditVo);

	public List<JobLockVo> getJobLockByServerId(Integer serverId);

	public JobVo getJobBaseInfoByUuid(String uuid);

	@Transactional
	public int saveJob(JobVo job);

	@Transactional
	public int deleteJob(String jobUuid);

	@Transactional
	public int resetRunningJobLockByServerId(Integer serverId);

}
