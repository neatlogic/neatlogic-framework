package codedriver.framework.scheduler.dao.mapper;

import java.util.List;

import codedriver.framework.scheduler.dto.JobAuditVo;
import codedriver.framework.scheduler.dto.JobClassVo;
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
	public JobVo getJobByName(JobVo job);
	public List<ServerNewJobVo> getNewJobByServerId(int serverId);
	
	public JobClassVo getJobClassByClasspath(JobClassVo jobClassVo);
	public int searchJobClassCount(JobClassVo jobClassVo);
	public List<JobClassVo> searchJobClassList(JobClassVo jobClassVo);
	
	public int searchJobAuditCount(JobAuditVo jobAuditVo);
	public List<JobAuditVo> searchJobAuditList(JobAuditVo jobAuditVo);
	public JobAuditVo getJobAuditLogById(Long auditId);
	
	public JobLockVo getJobLockByUuid(String uuid);
	//UPDATE
	public int updateJobById(JobVo job);
	public int updateJobStatusByJobUuid(JobStatusVo jobStatus);
	public int updateJobClass(JobClassVo jobClass);
	public int updateJobAudit(JobAuditVo scheduleJobAudit);
	public int updateJobLockByJobId(JobLockVo jobLock);
	public int updateJobLockByServerId(JobLockVo jobLock);
	//INSERT	
	public int insertJob(JobVo job);
	public int insertJobStatus(JobStatusVo jobStatus);
	public int insertJobProp(JobPropVo jobProp);
	public int insertJobClass(JobClassVo jobClass);	
	public int insertJobAudit(JobAuditVo scheduleJobAudit);
	public int insertJobLock(JobLockVo jobLock);
	public int insertServerNewJob(ServerNewJobVo serverNewJobVo);
	//DELETE
	public int deleteJobByUuid(String uuid);
//	public int deleteJobPropByJobUuid(String jobUuid);
//	public int deleteJobLock(String jobUuid);
	public int deleteServerNewJobById(Long id);
	
}
