package codedriver.framework.scheduler.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.common.config.Config;
import codedriver.framework.common.util.PageUtil;
import codedriver.framework.dto.ModuleVo;
import codedriver.framework.scheduler.core.SchedulerManager;
import codedriver.framework.scheduler.dao.mapper.SchedulerMapper;
import codedriver.framework.scheduler.dto.JobAuditVo;
import codedriver.framework.scheduler.dto.JobClassVo;
import codedriver.framework.scheduler.dto.JobLockVo;
import codedriver.framework.scheduler.dto.JobPropVo;
import codedriver.framework.scheduler.dto.JobVo;
import codedriver.framework.scheduler.exception.ScheduleJobNotFoundException;

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
		List<ModuleVo> activeModuleList = TenantContext.get().getActiveModuleList();
		Set<String> moduleIdSet = new HashSet<>();
		for(ModuleVo module : activeModuleList) {
			moduleIdSet.add(module.getId());
		}
		List<JobClassVo> jobClassList = SchedulerManager.getAllJobClassList();
		int rowNum = jobClassList.size();
		int pageCount = PageUtil.getPageCount(rowNum,jobClassVo.getPageSize());
		jobClassVo.setPageCount(pageCount);
		jobClassVo.setRowNum(rowNum);
		List<JobClassVo> jobClassFilterList = new ArrayList<>();
		for(JobClassVo jobClass : jobClassList) {
			if(!moduleIdSet.contains(jobClass.getModuleId())) {
				continue;
			}
			if(jobClassVo.getType() != null && !jobClass.getType().equals(jobClassVo.getType())) {
				continue;
			}
			if(jobClassVo.getModuleName() != null && !jobClass.getModuleName().contains(jobClassVo.getModuleName())) {
				continue;
			}
			if(jobClassVo.getName() != null && !jobClass.getName().contains(jobClassVo.getName())) {
				continue;
			}
			jobClassFilterList.add(jobClass);
		}
		
		int startNum = jobClassVo.getStartNum();
		int pageSize = jobClassVo.getPageSize();
		int endNum = startNum + pageSize - 1;
		int MaxNum = jobClassFilterList.size() - 1;
		endNum = endNum >  MaxNum ? MaxNum : endNum;
		List<JobClassVo> returnJobClassList = jobClassFilterList.subList(startNum, endNum + 1);
		return returnJobClassList;
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
			throw new ScheduleJobNotFoundException("定时作业："+ job.getName() + "不存在");
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
