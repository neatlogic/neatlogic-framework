package codedriver.framework.scheduler.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import codedriver.framework.common.config.Config;
import codedriver.framework.common.util.PageUtil;
import codedriver.framework.exception.core.ApiRuntimeException;
import codedriver.framework.exception.core.FrameworkExceptionMessageBase;
import codedriver.framework.exception.core.IApiExceptionMessage;
import codedriver.framework.exception.type.CustomExceptionMessage;
import codedriver.framework.scheduler.dao.mapper.SchedulerMapper;
import codedriver.framework.scheduler.dto.JobAuditVo;
import codedriver.framework.scheduler.dto.JobClassVo;
import codedriver.framework.scheduler.dto.JobLockVo;
import codedriver.framework.scheduler.dto.JobPropVo;
import codedriver.framework.scheduler.dto.JobVo;
import codedriver.framework.scheduler.exception.SchedulerExceptionMessage;

@Service
@Transactional
public class SchedulerServiceImpl implements SchedulerService{
		
	@Autowired 
	private SchedulerMapper schedulerMapper;
	
	@Override
	public List<JobVo> searchJobList(JobVo jobVo) {
		int rowNum = schedulerMapper.searchJobCount(jobVo);
		int pageCount = PageUtil.getPageCount(rowNum,jobVo.getPageSize());
		jobVo.setPageCount(pageCount);
		jobVo.setRowNum(rowNum);
		return schedulerMapper.searchJobList(jobVo);
	}

	@Override
	public List<JobClassVo> searchJobClassList(JobClassVo jobClassVo) {
		int rowNum = schedulerMapper.searchJobClassCount(jobClassVo);
		int pageCount = PageUtil.getPageCount(rowNum,jobClassVo.getPageSize());
		jobClassVo.setPageCount(pageCount);
		jobClassVo.setRowNum(rowNum);
		return schedulerMapper.searchJobClassList(jobClassVo);
	}

	@Override
	public List<JobAuditVo> searchJobAuditList(JobAuditVo jobAuditVo) {
		int rowNum = schedulerMapper.searchJobAuditCount(jobAuditVo);
		int pageCount = PageUtil.getPageCount(rowNum,jobAuditVo.getPageSize());
		jobAuditVo.setPageCount(pageCount);
		jobAuditVo.setRowNum(rowNum);
		return schedulerMapper.searchJobAuditList(jobAuditVo);
	}

	@Override
	public void saveJob(JobVo job) {
		String uuid = job.getUuid();
		schedulerMapper.deleteJobByUuid(uuid);
		JobVo jobVo = schedulerMapper.getJobByName(job);
		if(jobVo != null) {
			IApiExceptionMessage message = new FrameworkExceptionMessageBase(new SchedulerExceptionMessage(new CustomExceptionMessage("名称："+ job.getName() + " 已存在")));
			throw new ApiRuntimeException(message);
		}
		job.setUuid(null);
		uuid = job.getUuid();
		schedulerMapper.insertJob(job);
		
		for(JobPropVo jobProp : job.getPropList()) {
			jobProp.setJobUuid(uuid);
			schedulerMapper.insertJobProp(jobProp);
		}
	}

	@Override
	public void saveJobClass(JobClassVo jobClassVo) {
		JobClassVo jobClass = schedulerMapper.getJobClassByClasspath(jobClassVo);
		if(jobClass == null) {
			IApiExceptionMessage message = new FrameworkExceptionMessageBase(new SchedulerExceptionMessage(new CustomExceptionMessage("定时作业组件："+ jobClassVo.getClasspath() + " 不存在")));
			throw new ApiRuntimeException(message);
		}
		jobClass.setType(jobClassVo.getType());
		schedulerMapper.updateJobClass(jobClassVo);
	}

	@Override
	public boolean getJobLock(String uuid) {
		JobLockVo jobLock = schedulerMapper.getJobLockByUuid(uuid);
		if(jobLock == null) {
			return false;
		}
		if(JobLockVo.WAIT.equals(jobLock.getLock()) || jobLock.getServerId().intValue() == Config.SCHEDULE_SERVER_ID) {
			schedulerMapper.updateJobLockByJobId(new JobLockVo(uuid, JobLockVo.RUN, Config.SCHEDULE_SERVER_ID));
			return true;
		}		
		return false;
	}

}
