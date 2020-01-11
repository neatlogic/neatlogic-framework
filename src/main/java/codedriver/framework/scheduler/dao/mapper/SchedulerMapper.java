package codedriver.framework.scheduler.dao.mapper;

import java.util.List;

import codedriver.framework.scheduler.dto.JobAuditVo;
import codedriver.framework.scheduler.dto.JobLockVo;
import codedriver.framework.scheduler.dto.JobPropVo;
import codedriver.framework.scheduler.dto.JobStatusVo;
import codedriver.framework.scheduler.dto.JobVo;
import codedriver.framework.scheduler.dto.ServerNewJobVo;

public interface SchedulerMapper {
	//SELECT
	public JobVo getJobByUuid(String uuid);
	public JobStatusVo getJobStatusByJobUuid(String jobUuid);
	public List<JobVo> getJobByClasspath(String classpath);
	public int searchJobCount(JobVo jobVo);
	public List<JobVo> searchJobList(JobVo jobVo);
	public List<ServerNewJobVo> getServerJobByServerId(int serverId);
	
	public int searchJobAuditCount(JobAuditVo jobAuditVo);
	public List<JobAuditVo> searchJobAuditList(JobAuditVo jobAuditVo);
	public JobAuditVo getJobAuditLogById(Long auditId);
	
	public JobLockVo getJobLockByUuid(String uuid);
	public int checkJobNameIsRepeat(JobVo job);
	//UPDATE
	public int updateJobById(JobVo job);
	public int updateJobStatusByJobUuid(JobStatusVo jobStatus);
	public int updateJobAudit(JobAuditVo scheduleJobAudit);
	public int updateJobLockByJobId(JobLockVo jobLock);
	public int updateJobLockByServerId(JobLockVo jobLock);
	//INSERT	
	public int insertJob(JobVo job);
	public int insertJobStatus(JobStatusVo jobStatus);
	public int insertJobProp(JobPropVo jobProp);	
	public int insertJobAudit(JobAuditVo scheduleJobAudit);
	public int insertJobLock(JobLockVo jobLock);
	public int insertServerJob(ServerNewJobVo serverNewJobVo);
	//DELETE
	public int deleteJobByUuid(String uuid);
	public int deleteServerJobById(Long id);
	public int deleteServerJobByServerId(Integer serverId);
	public int deleteJobStatusAndLockByJobUuid(String jobUuid);
	
}
