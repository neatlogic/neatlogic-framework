package codedriver.framework.scheduler.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.common.config.Config;
import codedriver.framework.common.util.PageUtil;
import codedriver.framework.exception.ApiRuntimeException;

import codedriver.framework.scheduler.core.SchedulerManager;
import codedriver.framework.scheduler.dao.mapper.SchedulerMapper;
import codedriver.framework.scheduler.dto.JobAuditVo;
import codedriver.framework.scheduler.dto.JobClassVo;
import codedriver.framework.scheduler.dto.JobLockVo;
import codedriver.framework.scheduler.dto.JobPropVo;
import codedriver.framework.scheduler.dto.JobVo;
import codedriver.framework.scheduler.dto.ServerNewJobVo;
import codedriver.framework.scheduler.exception.SchedulerExceptionMessage;

@Service
@Transactional
public class SchedulerServiceImpl implements SchedulerService{

	private Logger logger = LoggerFactory.getLogger(SchedulerServiceImpl.class);
	
	@Autowired
	private SchedulerManager schedulerManager;
	
	@Autowired 
	private SchedulerMapper schedulerMapper;
	
//	@Autowired 
//	private ServerMapper serverMapper;
	
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
			schedulerMapper.insertJobLock(new JobLockVo(job.getId(), JobLockVo.RELEASE_LOCK));
			for(JobPropVo jobProp : job.getPropList()) {
				jobProp.setJobId(job.getId());
				schedulerMapper.insertJobProp(jobProp);
			}
			if(JobVo.RUNNING.equals(job.getIsActive())) {
				schedulerManager.loadJob(job);
//				TenantContext tenant = TenantContext.get();
//				tenant.setUseDefaultDatasource(true);
//				List<ServerClusterVo> serverList = serverMapper.getServerByStatus(ServerClusterVo.STARTUP);
//				for(ServerClusterVo server : serverList) {
//					int serverId = server.getServerId();
//					if(Config.SCHEDULE_SERVER_ID == serverId) {
//						continue;
//					}
//					schedulerMapper.insertServerNewJob(new ServerNewJobVo(serverId, job.getId(), tenant.getTenantUuid()));
//				}				
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
	public boolean getJobLock(Long jobId) {
		JobLockVo jobLock = schedulerMapper.getJobLockById(jobId);
		if(jobLock != null && JobLockVo.RELEASE_LOCK.equals(jobLock.getLock())) {
			schedulerMapper.updateJobLockByJobId(new JobLockVo(jobId, JobLockVo.GET_LOCK, Config.SCHEDULE_SERVER_ID));
			return true;
		}		
		return false;
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
		schedulerMapper.updateJobLockByJobId(new JobLockVo(jobId, JobLockVo.RELEASE_LOCK));
	}
	
	@Override
	public void deleteJob(Long jobId) {
		schedulerMapper.deleteJobLock(jobId);
		schedulerManager.deleteJob(jobId);
		schedulerMapper.deleteJobById(jobId);
		schedulerMapper.deleteJobPropByJobId(jobId);
	}
	

}
