package codedriver.framework.scheduler.dao.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import codedriver.framework.scheduler.dto.JobClassVo;
import codedriver.framework.scheduler.dto.JobVo;

public interface ScheduleMapper {
	//SELECT
	public JobVo getJobById(Long jobId);
	public int getJobClassVoCount(JobClassVo jobClass);
	public List<JobVo> getJobByClassId(@Param("classId")Integer classId, @Param("serverId")Integer serverId);

	//UPDATE
	public int updateJobStatus(JobVo jobVo);
	public int updateJobClass(JobClassVo jobClass);
	public int updateScheduleJobTimeById(JobVo scheduleJobVo);
	public int updateServerId(@Param("newServerId") Integer newServerId, @Param("oldServerId") Integer oldServerId);
	
	//INSERT
	public int insertJobClass(JobClassVo jobClass);
	
}
