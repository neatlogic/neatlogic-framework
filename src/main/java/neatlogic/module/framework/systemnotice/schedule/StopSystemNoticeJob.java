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

package neatlogic.module.framework.systemnotice.schedule;

import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.common.constvalue.systemuser.SystemUser;
import neatlogic.framework.scheduler.core.JobBase;
import neatlogic.framework.scheduler.dto.JobObject;
import neatlogic.framework.systemnotice.dao.mapper.SystemNoticeMapper;
import neatlogic.framework.systemnotice.dto.SystemNoticeVo;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

@Component
@DisallowConcurrentExecution
public class StopSystemNoticeJob extends JobBase {

    @Resource
    private SystemNoticeMapper systemNoticeMapper;


    @Override
    public String getGroupName() {
        return TenantContext.get().getTenantUuid() + "-STOP-SYSTEMNOTICE";
    }

    @Override
    public void reloadJob(JobObject jobObject) {
        String tenantUuid = jobObject.getTenantUuid();
        TenantContext.get().switchTenant(tenantUuid);
        Long noticeId = Long.valueOf(jobObject.getJobName());
        SystemNoticeVo systemNotice = systemNoticeMapper.getSystemNoticeById(noticeId);
        if (systemNotice != null && Objects.equals(systemNotice.getStatus(), SystemNoticeVo.Status.ISSUED.getValue()) && systemNotice.getEndTime() != null) {
            JobObject.Builder newJobObjectBuilder = new JobObject.Builder(noticeId.toString(), this.getGroupName(), this.getClassName(), TenantContext.get().getTenantUuid())
                    .withBeginTime(systemNotice.getEndTime())
                    .withIntervalInSeconds(60 * 60)
                    .withRepeatCount(0);
            JobObject newJobObject = newJobObjectBuilder.build();
            schedulerManager.loadJob(newJobObject);
        }
    }

    @Override
    public void initJob(String tenantUuid) {
        List<Long> noticeIdList = systemNoticeMapper.getIssuedNoticeIdList();
        for(Long noticeId : noticeIdList) {
            JobObject.Builder jobObjectBuilder = new JobObject.Builder(noticeId.toString(), this.getGroupName(), this.getClassName(), TenantContext.get().getTenantUuid());
            JobObject jobObject = jobObjectBuilder.build();
            this.reloadJob(jobObject);
        }
    }

    @Override
    protected Boolean isMyHealthy(JobObject jobObject) {
        Long noticeId = Long.valueOf(jobObject.getJobName());
        SystemNoticeVo systemNotice = systemNoticeMapper.getSystemNoticeById(noticeId);
        if (systemNotice != null && Objects.equals(systemNotice.getStatus(), SystemNoticeVo.Status.ISSUED.getValue()) && systemNotice.getEndTime() != null) {
            return true;
        }
        return false;
    }

    @Override
    public void executeInternal(JobExecutionContext context, JobObject jobObject) throws Exception {
        Long noticeId = Long.valueOf(jobObject.getJobName());
        SystemNoticeVo systemNotice = systemNoticeMapper.getSystemNoticeById(noticeId);
        if (systemNotice != null && Objects.equals(systemNotice.getStatus(), SystemNoticeVo.Status.ISSUED.getValue()) && systemNotice.getStartTime() != null) {
            systemNotice.setLcu(SystemUser.SYSTEM.getUserUuid());
            systemNoticeMapper.stopSystemNoticeById(systemNotice);
        }
    }
}
