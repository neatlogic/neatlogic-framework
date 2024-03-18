/*Copyright (C) $today.year  深圳极向量科技有限公司 All Rights Reserved.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.*/

package neatlogic.module.framework.scheduler.heartbreak.handler;

import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.dao.mapper.TenantMapper;
import neatlogic.framework.dto.TenantVo;
import neatlogic.framework.heartbeat.core.IHeartbreakHandler;
import neatlogic.framework.scheduler.core.IJob;
import neatlogic.framework.scheduler.core.SchedulerManager;
import neatlogic.framework.scheduler.dao.mapper.SchedulerMapper;
import neatlogic.framework.scheduler.dto.JobLockVo;
import neatlogic.framework.scheduler.dto.JobObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
