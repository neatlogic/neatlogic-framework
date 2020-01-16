package codedriver.framework.scheduler.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import codedriver.framework.common.config.Config;
import codedriver.framework.common.util.PageUtil;
import codedriver.framework.scheduler.core.SchedulerManager;
import codedriver.framework.scheduler.dao.mapper.SchedulerMapper;
import codedriver.framework.scheduler.dto.JobAuditVo;
import codedriver.framework.scheduler.dto.JobClassVo;
import codedriver.framework.scheduler.dto.JobLockVo;
import codedriver.framework.scheduler.dto.JobPropVo;
import codedriver.framework.scheduler.dto.JobVo;
import codedriver.framework.scheduler.exception.ScheduleJobNameRepeatException;
import codedriver.framework.scheduler.exception.ScheduleJobNotFoundException;

@Service
@Transactional
public class SchedulerServiceImpl implements SchedulerService{
		
	@Autowired 
	private SchedulerMapper schedulerMapper;
	
	@Override
	public List<JobVo> searchJobList(JobVo jobVo) {
		int rowNum = schedulerMapper.searchJobCount(jobVo);
		int pageCount = PageUtil.getPageCount(rowNum, jobVo.getPageSize());
		jobVo.setPageCount(pageCount);
		jobVo.setRowNum(rowNum);
		return schedulerMapper.searchJobList(jobVo);
	}

	@Override
	public List<JobClassVo> searchJobClassList(JobClassVo jobClassVo) {
		List<JobClassVo> jobClassList = SchedulerManager.getAllJobClassList();		
		List<JobClassVo> jobClassFilterList = new ArrayList<>();
		for(JobClassVo jobClass : jobClassList) {
			if(jobClassVo.getType() != null && !jobClass.getType().equals(jobClassVo.getType())) {
				continue;
			}
			if(jobClassVo.getModuleId() != null && !jobClass.getModuleId().equals(jobClassVo.getModuleId())) {
				continue;
			}
			if(jobClassVo.getKeyword() != null && !jobClass.getName().contains(jobClassVo.getKeyword())) {
				continue;
			}
			jobClassFilterList.add(jobClass);
		}
				
		int pageSize = jobClassVo.getPageSize();
		int rowNum = jobClassFilterList.size();
		int pageCount = PageUtil.getPageCount(rowNum, pageSize);
		jobClassVo.setPageCount(pageCount);
		jobClassVo.setRowNum(rowNum);
		int startNum = jobClassVo.getStartNum();
		int endNum = startNum + pageSize;
		endNum = endNum >  rowNum ? rowNum : endNum;
		List<JobClassVo> returnJobClassList = jobClassFilterList.subList(startNum, endNum);
		return returnJobClassList;
	}

	@Override
	public List<JobAuditVo> searchJobAuditList(JobAuditVo jobAuditVo) {
		JobVo job = schedulerMapper.getJobByUuid(jobAuditVo.getJobUuid());
		if(job == null) {
			throw new ScheduleJobNotFoundException(jobAuditVo.getJobUuid());
		}
		int rowNum = schedulerMapper.searchJobAuditCount(jobAuditVo);
		int pageCount = PageUtil.getPageCount(rowNum, jobAuditVo.getPageSize());
		jobAuditVo.setPageCount(pageCount);
		jobAuditVo.setRowNum(rowNum);
		return schedulerMapper.searchJobAuditList(jobAuditVo);
	}

	@Override
	public void saveJob(JobVo job) {
		String uuid = job.getUuid();
		if(schedulerMapper.checkJobNameIsRepeat(job) == 0) {
			throw new ScheduleJobNameRepeatException(job.getName());
		}
		schedulerMapper.deleteJobByUuid(uuid);
		job.setUuid(null);
		uuid = job.getUuid();
		schedulerMapper.insertJob(job);
		
		for(JobPropVo jobProp : job.getPropList()) {
			jobProp.setJobUuid(uuid);
			schedulerMapper.insertJobProp(jobProp);
		}
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
