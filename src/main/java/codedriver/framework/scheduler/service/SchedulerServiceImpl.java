package codedriver.framework.scheduler.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import codedriver.framework.common.util.PageUtil;
import codedriver.framework.exception.ApiRuntimeException;

import codedriver.framework.scheduler.core.SchedulerManager;
import codedriver.framework.scheduler.dao.mapper.SchedulerMapper;
import codedriver.framework.scheduler.dto.JobAuditVo;
import codedriver.framework.scheduler.dto.JobClassVo;
import codedriver.framework.scheduler.dto.JobPropVo;
import codedriver.framework.scheduler.dto.JobVo;
import codedriver.framework.scheduler.exception.SchedulerExceptionMessage;

@Service
@Transactional
public class SchedulerServiceImpl implements SchedulerService{

	private Logger logger = LoggerFactory.getLogger(SchedulerServiceImpl.class);
	
	@Autowired
	private SchedulerManager schedulerManager;
	
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
	public int saveJob(JobVo job) {
		JobVo jobVo = schedulerMapper.getJobByName(job);
		if(jobVo != null) {
			SchedulerExceptionMessage message = new SchedulerExceptionMessage("名称："+ job.getName() + " 已存在");
			logger.error(message.toString());
			throw new ApiRuntimeException(message);
		}
		int count = 0;
		if(job.getId() != null) {
			deleteJob(job.getId());					
		}
		count = schedulerMapper.insertJob(job);		
		if(count == 1) {
			schedulerMapper.insertJobLock(job.getId());
			for(JobPropVo jobProp : job.getPropList()) {
				jobProp.setJobId(job.getId());
				schedulerMapper.insertJobProp(jobProp);
			}
			if(JobVo.RUNNING.equals(job.getIsActive())) {
				schedulerManager.loadJob(job);
			}			
		}
		return count;
	}

	@Override
	public int saveJobClass(JobClassVo jobClassVo) {
		JobClassVo jobClass = schedulerMapper.getJobClassByClasspath(jobClassVo);
		if(jobClass == null) {
			SchedulerExceptionMessage message = new SchedulerExceptionMessage("定时作业组件："+ jobClassVo.getClasspath() + " 不存在");
			logger.error(message.toString());
			throw new ApiRuntimeException(message);
		}
		jobClass.setType(jobClassVo.getType());
		return schedulerMapper.updateJobClass(jobClassVo);
	}

	@Override
	public int getJobLock(Long jobId, int serverId) {
		int count = schedulerMapper.updateJobLock(jobId, JobVo.GET_LOCK);
		if(count > 0) {
			JobVo jobVo = new JobVo();
			jobVo.setId(jobId);
			jobVo.setServerId(serverId);
			schedulerMapper.updateJobById(jobVo);
		}
		return count;
	}

	@Override
	public void loadJob(JobVo jobVo) {
		JobVo updateJob = new JobVo();
		updateJob.setId(jobVo.getId());
		updateJob.setStatus(JobVo.RUNNING);
		schedulerMapper.updateJobById(updateJob);
		schedulerManager.loadJob(jobVo);
	}
	
	@Override
	public void stopJob(Long jobId) {
		JobVo jobVo = new JobVo();
		jobVo.setId(jobId);
		jobVo.setStatus(JobVo.STOP);
		schedulerMapper.updateJobById(jobVo);
		schedulerManager.deleteJob(jobId);
		schedulerMapper.updateJobLock(jobId, JobVo.RELEASE_LOCK);
	}
	
	@Override
	public void deleteJob(Long jobId) {
		schedulerMapper.deleteJobLock(jobId);
		schedulerManager.deleteJob(jobId);
		schedulerMapper.deleteJobById(jobId);
		schedulerMapper.deleteJobPropByJobId(jobId);
	}
	

}
