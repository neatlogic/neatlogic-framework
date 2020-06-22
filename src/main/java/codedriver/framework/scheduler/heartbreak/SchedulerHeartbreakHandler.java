package codedriver.framework.scheduler.heartbreak;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.dao.mapper.TenantMapper;
import codedriver.framework.dto.TenantVo;
import codedriver.framework.heartbeat.core.IHeartbreakHandler;
import codedriver.framework.scheduler.core.IJob;
import codedriver.framework.scheduler.core.SchedulerManager;
import codedriver.framework.scheduler.dao.mapper.SchedulerMapper;
import codedriver.framework.scheduler.dto.JobLockVo;
import codedriver.framework.scheduler.dto.JobObject;

@Service
public class SchedulerHeartbreakHandler implements IHeartbreakHandler {

	@Autowired
	private SchedulerManager schedulerManager;

	@Autowired
	private SchedulerMapper schedulerMapper;

	@Autowired
	private TenantMapper tenantMapper;

	@Override
	public void whenServerInactivated(Integer serverId) {
		//切换到核心库
		TenantContext.get().setUseDefaultDatasource(true);
		List<TenantVo> tenantList = tenantMapper.getAllActiveTenant();
		
		for (TenantVo tenantVo : tenantList) {
			// 切换到租户库
			TenantContext.get().switchTenant(tenantVo.getUuid()).setUseDefaultDatasource(false);
			// 重置异常server的作业锁状态为waiting
			schedulerMapper.resetJobLockByServerId(serverId);
			// 接管异常server的作业
			List<JobLockVo> jobLockList = schedulerMapper.getJobLockByServerId(serverId);
			// TODO 获取所有tenant
			for (JobLockVo jobLockVo : jobLockList) {
				if (!schedulerManager.checkJobIsExists(jobLockVo.getJobName(), jobLockVo.getJobGroup())) {
					IJob jobHandler = SchedulerManager.getHandler(jobLockVo.getJobHandler());
					if (jobHandler != null) {
						JobObject jobObject = new JobObject.Builder(jobLockVo.getJobName(), jobLockVo.getJobGroup(), jobLockVo.getJobHandler(), tenantVo.getUuid()).build();
						jobHandler.reloadJob(jobObject);
					}
				}
			}
		}
	}

}
