package codedriver.framework.scheduler.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import codedriver.framework.common.config.Config;
import codedriver.framework.common.constvalue.ModuleEnum;
import codedriver.framework.common.util.PageUtil;
import codedriver.framework.scheduler.core.SchedulerManager;
import codedriver.framework.scheduler.dao.mapper.SchedulerMapper;
import codedriver.framework.scheduler.dto.JobAuditVo;
import codedriver.framework.scheduler.dto.JobClassVo;
import codedriver.framework.scheduler.dto.JobLockVo;
import codedriver.framework.scheduler.dto.JobPropVo;
import codedriver.framework.scheduler.dto.JobStatusVo;
import codedriver.framework.scheduler.dto.JobVo;
import codedriver.framework.scheduler.exception.ScheduleJobNameRepeatException;
import codedriver.framework.scheduler.exception.ScheduleJobNotFoundException;

@Service
public class SchedulerServiceImpl implements SchedulerService {

	@Autowired
	private SchedulerMapper schedulerMapper;

	@Override
	public List<JobVo> searchJobList(JobVo jobVo) {
		int rowNum = schedulerMapper.searchJobCount(jobVo);
		int pageCount = PageUtil.getPageCount(rowNum, jobVo.getPageSize());
		jobVo.setPageCount(pageCount);
		jobVo.setRowNum(rowNum);
		return schedulerMapper.searchJob(jobVo);
	}

	@Override
	public List<JobClassVo> searchJobClassList(JobClassVo jobClassVo) {
		List<JobClassVo> jobClassList = SchedulerManager.getAllPublicJobClassList();
		List<JobClassVo> jobClassFilterList = new ArrayList<>();
		List<String> moduleList = ModuleEnum.getModuleList(jobClassVo.getModuleId());
		for (JobClassVo jobClass : jobClassList) {
			if (CollectionUtils.isNotEmpty(moduleList) && !moduleList.contains(jobClass.getModuleId())) {
				continue;
			}
			if (jobClassVo.getKeyword() != null && !jobClass.getName().contains(jobClassVo.getKeyword())) {
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
		endNum = endNum > rowNum ? rowNum : endNum;
		List<JobClassVo> returnJobClassList = jobClassFilterList.subList(startNum, endNum);
		return returnJobClassList;
	}

	@Override
	public List<JobAuditVo> searchJobAudit(JobAuditVo jobAuditVo) {
		JobVo job = schedulerMapper.getJobByUuid(jobAuditVo.getJobUuid());
		if (job == null) {
			throw new ScheduleJobNotFoundException(jobAuditVo.getJobUuid());
		}
		int rowNum = schedulerMapper.searchJobAuditCount(jobAuditVo);
		int pageCount = PageUtil.getPageCount(rowNum, jobAuditVo.getPageSize());
		jobAuditVo.setPageCount(pageCount);
		jobAuditVo.setRowNum(rowNum);
		return schedulerMapper.searchJobAudit(jobAuditVo);
	}

	@Override
	public int updateJobLockAndStatus(JobLockVo jobLockVo, JobStatusVo jobStatusVo) {
		schedulerMapper.updateJobStatus(jobStatusVo);
		return schedulerMapper.updateJobLock(jobLockVo);
	}

	@Override
	public int saveJob(JobVo job) {
		String uuid = job.getUuid();
		if (schedulerMapper.checkJobNameIsExists(job) > 0) {
			throw new ScheduleJobNameRepeatException(job.getName());
		}
		JobVo oldJobVo = schedulerMapper.getJobBaseInfoByUuid(uuid);
		if (oldJobVo == null) {
			schedulerMapper.insertJob(job);
		} else {
			schedulerMapper.deleteJobPropByJobUuid(uuid);
			schedulerMapper.updateJob(job);
		}
		if (job.getPropList() != null && job.getPropList().size() > 0) {
			for (JobPropVo jobProp : job.getPropList()) {
				jobProp.setJobUuid(uuid);
				schedulerMapper.insertJobProp(jobProp);
			}
		}
		return 1;
	}

	@Override
	public JobLockVo getJobLock(String jobName, String jobGroup) {
		JobLockVo jobLockVo = schedulerMapper.getJobLockByJobNameGroup(jobName, jobGroup);
		if (jobLockVo == null) {
			// 没有锁的情况证明作业已经被删除
			return null;
		} else {
			// 如果锁的状态是running状态，证明其他节点已经在执行，直接返回
			if (jobLockVo.getLock().equals(JobLockVo.RUNNING) && !jobLockVo.getServerId().equals(Config.SCHEDULE_SERVER_ID)) {
				return null;
			}
		}
		// 修改锁状态
		jobLockVo.setServerId(Config.SCHEDULE_SERVER_ID);
		jobLockVo.setLock(JobLockVo.RUNNING);
		schedulerMapper.updateJobLock(jobLockVo);
		return jobLockVo;
	}

	@Override
	public int resetRunningJobLockByServerId(Integer serverId) {
		// 重置该serverid的作业锁状态为waiting
		schedulerMapper.resetJobLockByServerId(serverId);
		return 1;
	}

	@Override
	public List<JobLockVo> getJobLockByServerId(Integer serverId) {
		return schedulerMapper.getJobLockByServerId(serverId);
	}

	@Override
	public int deleteJob(String jobUuid) {
		schedulerMapper.deleteJobAuditByJobUuid(jobUuid);
		schedulerMapper.deleteJobPropByJobUuid(jobUuid);
		schedulerMapper.deleteJobByUuid(jobUuid);
		return 1;
	}

	@Override
	public JobVo getJobBaseInfoByUuid(String uuid) {
		return schedulerMapper.getJobBaseInfoByUuid(uuid);
	}

}
