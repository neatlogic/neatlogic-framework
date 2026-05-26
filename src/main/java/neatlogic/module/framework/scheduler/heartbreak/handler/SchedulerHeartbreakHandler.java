/*
 *
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the NeatLogic Sustainable Use License (NSUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */

package neatlogic.module.framework.scheduler.heartbreak.handler;

import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.common.config.Config;
import neatlogic.framework.dao.mapper.TenantMapper;
import neatlogic.framework.dto.TenantVo;
import neatlogic.framework.heartbeat.core.IHeartbreakHandler;
import neatlogic.framework.scheduler.core.IJob;
import neatlogic.framework.scheduler.core.SchedulerManager;
import neatlogic.framework.scheduler.dao.mapper.SchedulerMapper;
import neatlogic.framework.scheduler.dto.JobLockVo;
import neatlogic.framework.scheduler.dto.JobObject;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class SchedulerHeartbreakHandler implements IHeartbreakHandler {

	@Resource
	private SchedulerManager schedulerManager;

	@Resource
	private SchedulerMapper schedulerMapper;

	@Resource
	private TenantMapper tenantMapper;

	@Override
	public void whenServerInactivated(Integer serverId) {
		//切换到核心库
		List<TenantVo> tenantList = tenantMapper.getAllActiveTenant();
		
		for (TenantVo tenantVo : tenantList) {
			// 切换到租户库
			TenantContext.get().switchTenant(tenantVo.getUuid());
			// 重置异常server的作业锁状态为waiting
			schedulerMapper.resetJobLockByServerId(serverId);
			schedulerMapper.deleteJobLoadByServerId(serverId);
			// 接管异常server的作业
			List<JobLockVo> jobLockList = schedulerMapper.getJobLockByServerId(serverId);
			for (JobLockVo jobLockVo : jobLockList) {
				jobLockVo.setServerId(Config.SCHEDULE_SERVER_ID);
				schedulerMapper.updateJobLock(jobLockVo);
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
