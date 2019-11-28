package codedriver.framework.scheduler.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import codedriver.framework.common.config.Config;
import codedriver.framework.common.util.PageUtil;
import codedriver.framework.exception.ApiRuntimeException;

import codedriver.framework.scheduler.core.SchedulerManager;
import codedriver.framework.scheduler.dao.mapper.SchedulerMapper;
import codedriver.framework.scheduler.dto.JobAuditVo;
import codedriver.framework.scheduler.dto.JobClassVo;
import codedriver.framework.scheduler.dto.JobLockVo;
import codedriver.framework.scheduler.dto.JobObject;
import codedriver.framework.scheduler.dto.JobPropVo;
import codedriver.framework.scheduler.dto.JobVo;
import codedriver.framework.scheduler.exception.SchedulerExceptionMessage;

@Service
@Transactional
public class SchedulerServiceImpl implements SchedulerService{

	private Logger logger = LoggerFactory.getLogger(SchedulerServiceImpl.class);
	

	
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
		String uuid = job.getUuid();
		schedulerMapper.deleteJobByUuid(uuid);
		JobVo jobVo = schedulerMapper.getJobByName(job);
		if(jobVo != null) {
			SchedulerExceptionMessage message = new SchedulerExceptionMessage("名称："+ job.getName() + " 已存在");
			logger.error(message.toString());
			throw new ApiRuntimeException(message);
		}
		job.setUuid(null);						
		int count = schedulerMapper.insertJob(job);
		schedulerMapper.insertJobLock(new JobLockVo(uuid, JobLockVo.RELEASE_LOCK, Config.SCHEDULE_SERVER_ID));
		for(JobPropVo jobProp : job.getPropList()) {
			jobProp.setJobUuid(uuid);
			schedulerMapper.insertJobProp(jobProp);
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
	public boolean getJobLock(String uuid) {
		JobLockVo jobLock = schedulerMapper.getJobLockByUuid(uuid);
		if(jobLock != null && (JobLockVo.RELEASE_LOCK.equals(jobLock.getLock()) || jobLock.getServerId() == Config.SCHEDULE_SERVER_ID)) {
			schedulerMapper.updateJobLockByJobId(new JobLockVo(uuid, JobLockVo.GET_LOCK, Config.SCHEDULE_SERVER_ID));
			return true;
		}		
		return false;
	}

}
