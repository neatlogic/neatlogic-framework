package codedriver.framework.scheduler.dao.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import codedriver.framework.scheduler.dto.JobAuditVo;
import codedriver.framework.scheduler.dto.JobLockVo;
import codedriver.framework.scheduler.dto.JobPropVo;
import codedriver.framework.scheduler.dto.JobStatusVo;
import codedriver.framework.scheduler.dto.JobVo;

public interface SchedulerMapper {
	// SELECT
	public JobVo getJobByUuid(String uuid);

	public JobVo getJobBaseInfoByUuid(String uuid);

	public JobStatusVo getJobStatusByJobNameGroup(@Param("jobName")
	String jobName, @Param("jobGroup")
	String jobGroup);

	public List<JobVo> getJobByClassName(String className);

	public int searchJobCount(JobVo jobVo);

	public List<JobVo> searchJob(JobVo jobVo);

	public int searchJobAuditCount(JobAuditVo jobAuditVo);

	public List<JobAuditVo> searchJobAuditList(JobAuditVo jobAuditVo);

	public JobAuditVo getJobAuditById(Long auditId);

	public JobLockVo getJobLockByJobNameGroup(@Param("jobName")
	String jobName, @Param("jobGroup")
	String jobGroup);

	public List<JobLockVo> getJobLockByServerId(Integer serverId);

	public int checkJobNameIsExists(JobVo job);

	// UPDATE
	public int updateJob(JobVo jobVo);

	public int resetJobLockByServerId(Integer serverId);

	public int updateJobStatus(JobStatusVo jobStatus);

	public int updateJobAudit(JobAuditVo scheduleJobAudit);

	public int updateJobLock(JobLockVo jobLock);

	public int updateJobLockByServerId(JobLockVo jobLock);

	// INSERT
	public int insertJob(JobVo job);

	public int insertJobStatus(JobStatusVo jobStatus);

	public int insertJobProp(JobPropVo jobProp);

	public int insertJobAudit(JobAuditVo scheduleJobAudit);

	public int insertJobLock(JobLockVo jobLock);

	// DELETE
	public int deleteJobByUuid(String uuid);

	public int deleteJobPropByJobUuid(String jobUuid);

	public int deleteJobAuditByJobUuid(String jobUuid);

	public int deleteJobStatus(@Param("jobName")
	String jobName, @Param("jobGroup")
	String jobGroup);

	public int deleteJobLock(@Param("jobName")
	String jobName, @Param("jobGroup")
	String jobGroup);

}
