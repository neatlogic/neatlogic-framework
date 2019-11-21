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
	public List<JobVo> getJobByClasspath(@Param("classpath")String classpath, @Param("serverId")Integer serverId);
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
	public int updateServerId(@Param("classpath") String classpath, @Param("newServerId") Integer newServerId, @Param("oldServerId") Integer oldServerId);
	public int updateJobAudit(JobAuditVo scheduleJobAudit);	
	//INSERT	
	public int insertJob(JobVo job);
	public void insertJobProp(JobPropVo jobProp);
	public int insertJobClass(JobClassVo jobClass);	
	public int insertJobAudit(JobAuditVo scheduleJobAudit);
	//DELETE
	public void deleteJobById(Long jobId);
	public void deleteJobPropByJobId(Long jobId);
	
	
}
