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

package neatlogic.module.framework.service;

import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.asynchronization.threadlocal.UserContext;
import neatlogic.framework.crossover.IScheduleCrossoverService;
import neatlogic.framework.dao.mapper.TenantMapper;
import neatlogic.framework.exception.core.ApiRuntimeException;
import neatlogic.framework.exception.schedule.SchedulePublicAuthException;
import neatlogic.framework.heartbeat.dao.mapper.ServerMapper;
import neatlogic.framework.heartbeat.dto.ServerClusterVo;
import neatlogic.framework.scheduler.core.IJob;
import neatlogic.framework.scheduler.core.PublicJobBase;
import neatlogic.framework.scheduler.core.SchedulerManager;
import neatlogic.framework.scheduler.dao.mapper.SchedulerMapper;
import neatlogic.framework.scheduler.dto.JobLoadVo;
import neatlogic.framework.scheduler.dto.JobObject;
import neatlogic.framework.scheduler.dto.JobVo;
import neatlogic.framework.scheduler.exception.ScheduleHandlerNotFoundException;
import neatlogic.framework.scheduler.exception.ScheduleJobNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ScheduleServiceImpl implements IScheduleCrossoverService {
    @Resource
    private SchedulerManager schedulerManager;
    @Resource
    private SchedulerMapper schedulerMapper;
    @Resource
    private ServerMapper serverMapper;

    @Override
    public void scheduleTest(String jobHandlerClassName, String jobUuid, String type) throws Exception {
        IJob jobHandler = SchedulerManager.getHandler(jobHandlerClassName);
        if (jobHandler == null) {
            throw new ScheduleHandlerNotFoundException(jobHandlerClassName);
        }
        if (Objects.equals("public", type) && !(jobHandler instanceof PublicJobBase)) {
            throw new SchedulePublicAuthException();
        }
        String tenantUuid = TenantContext.get().getTenantUuid();
        JobVo jobVo = jobHandler.getJob(jobUuid);
        if (jobVo == null) {
            throw new ScheduleJobNotFoundException(jobUuid);
        }
        if (Objects.equals(jobVo.getIsActive(), 1)) {
            throw new ApiRuntimeException("状态为‘禁用’，才能执行测试");
        }
        JobObject jobObject = new JobObject.Builder(jobVo.getUuid(), jobHandler.getGroupName(), jobHandler.getClassName(), tenantUuid)
                .withRepeatCount(1)
                .needAudit(1)
                .withPropList(jobVo.getPropList())
                .setIsTest(1)
                .setTestUser(UserContext.get().getUserUuid(true))
                .setType(jobHandler.getType()).build();
        schedulerManager.loadJob(jobObject);
    }

    @Override
    public List<JobLoadVo> getJobLoadList(String jobName, String jobGroup) {
        List<ServerClusterVo> allServerList = serverMapper.getAllServerList();
        List<JobLoadVo> jobLoadList = schedulerMapper.getJobLoadListByJobNameGroup(jobName, jobGroup);
        List<JobLoadVo> resultList = new ArrayList<>();
        Map<Integer, JobLoadVo> serverId2jobLoadMap = jobLoadList.stream().collect(Collectors.toMap(JobLoadVo::getServerId, e -> e));
        for (ServerClusterVo serverClusterVo : allServerList) {
            if (Objects.equals(serverClusterVo.getStatus(), ServerClusterVo.STARTUP)) {
                JobLoadVo jobLoadVo = serverId2jobLoadMap.get(serverClusterVo.getServerId());
                if (jobLoadVo != null) {
                    if (Objects.equals(serverClusterVo.getStartTime(), jobLoadVo.getServerStartTime())) {
                        jobLoadVo.setIsLoad(1);
                    } else {
                        jobLoadVo.setIsLoad(0);
                    }
                } else {
                    jobLoadVo = new JobLoadVo();
                    jobLoadVo.setJobName(jobName);
                    jobLoadVo.setJobGroup(jobGroup);
                    jobLoadVo.setServerId(serverClusterVo.getServerId());
                    jobLoadVo.setServerStartTime(serverClusterVo.getStartTime());
                    jobLoadVo.setIsLoad(0);
                }
                resultList.add(jobLoadVo);
            }
        }
        return resultList;
    }

}
