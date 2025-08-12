/*
 * Copyright (C) 2025  深圳极向量科技有限公司 All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package neatlogic.module.framework.service;

import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.asynchronization.threadlocal.UserContext;
import neatlogic.framework.crossover.IScheduleCrossoverService;
import neatlogic.framework.exception.core.ApiRuntimeException;
import neatlogic.framework.exception.schedule.SchedulePublicAuthException;
import neatlogic.framework.scheduler.core.IJob;
import neatlogic.framework.scheduler.core.PublicJobBase;
import neatlogic.framework.scheduler.core.SchedulerManager;
import neatlogic.framework.scheduler.dto.JobObject;
import neatlogic.framework.scheduler.dto.JobVo;
import neatlogic.framework.scheduler.exception.ScheduleHandlerNotFoundException;
import neatlogic.framework.scheduler.exception.ScheduleJobNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

@Service
public class ScheduleServiceImpl implements IScheduleCrossoverService {
    @Resource
    private SchedulerManager schedulerManager;

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

}
