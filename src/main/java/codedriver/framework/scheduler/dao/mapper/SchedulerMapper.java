package codedriver.framework.scheduler.dao.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import codedriver.framework.scheduler.dto.JobAuditVo;
import codedriver.framework.scheduler.dto.JobClassVo;
import codedriver.framework.scheduler.dto.JobPropVo;
import codedriver.framework.scheduler.dto.JobVo;

public interface SchedulerMapper {
	//SELECT
	public JobVo getJobById(Long jobId);	
	public List<JobVo> getJobByClasspath(String classpath);
	public int searchJobCount(JobVo jobVo);
	public List<JobVo> searchJobList(JobVo jobVo);	
	public JobVo getJobByName(JobVo job);
	
	public JobClassVo getJobClassByClasspath(JobClassVo jobClassVo);
	public int searchJobClassCount(JobClassVo jobClassVo);
	public List<JobClassVo> searchJobClassList(JobClassVo jobClassVo);
	
	public int searchJobAuditCount(JobAuditVo jobAuditVo);
	public List<JobAuditVo> searchJobAuditList(JobAuditVo jobAuditVo);
	public JobAuditVo getJobAuditLogById(Long auditId);
	//UPDATE
	public int updateJobById(JobVo job);
	public int updateJobClass(JobClassVo jobClass);
	public int updateJobAudit(JobAuditVo scheduleJobAudit);
	public int updateJobLock(@Param("jobId")Long jobId, @Param("lock")String lock);	
	//INSERT	
	public int insertJob(JobVo job);
	public int insertJobProp(JobPropVo jobProp);
	public int insertJobClass(JobClassVo jobClass);	
	public int insertJobAudit(JobAuditVo scheduleJobAudit);
	public int insertJobLock(Long jobId);
	//DELETE
	public int deleteJobById(Long jobId);
	public int deleteJobPropByJobId(Long jobId);
	public int deleteJobLock(Long jobId);
}
