package codedriver.framework.dao.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import codedriver.framework.scheduler.dto.JobClassVo;
import codedriver.framework.scheduler.dto.JobPropVo;
import codedriver.framework.scheduler.dto.JobVo;

public interface ScheduleMapper {
	//SELECT
	public JobVo getJobById(Long jobId);
	public JobVo getJobByName(JobVo job);
	public int searchJobCount(JobVo scheduleJobVo);
	public int getJobClassCount(JobClassVo jobClass);
	public int getJobClassVoCount(JobClassVo jobClass);

	public List<JobVo> searchJob(JobVo scheduleJobVo);
	public List<JobClassVo> getJobClassList(JobClassVo jobClass);
	public List<JobClassVo> getAllJobClassList(@Param("typeList") int[] type);
	public List<JobVo> getJobByClassId(@Param("classId")Integer classId, @Param("serverId")Integer serverId);
	public JobClassVo getJobClass(JobClassVo jobClass);

	//UPDATE
	public int updateJob(JobVo jobVo);
	public int updateJobProp(JobPropVo prop);
	public int updateJobStatus(JobVo jobVo);
	public int updateJobClass(JobClassVo jobClass);
	public int updateJobClassType(JobClassVo jobClass);
	public int updateScheduleJobTimeById(JobVo scheduleJobVo);
//	public int resetJobStatus(Integer serverId);
	public int resetJobStatusNotStart(Integer serverId);
	
	//INSERT
	public int insertJob(JobVo jobVo);
	public int insertJobProp(JobPropVo prop);
	public int insertJobClass(JobClassVo jobClass);

	//DELETE
	public int deleteJob(Long jobId);
	public int deleteJobClass(Long id);
	public int deleteJobPropByJobId(Long jobId);
	
	
}
