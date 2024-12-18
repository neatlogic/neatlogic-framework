/*
 * Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.
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

package neatlogic.module.framework.systemnotice.schedule;

import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.scheduler.core.JobBase;
import neatlogic.framework.scheduler.dto.JobObject;
import neatlogic.framework.systemnotice.dao.mapper.SystemNoticeMapper;
import neatlogic.framework.systemnotice.dto.SystemNoticeVo;
import neatlogic.module.framework.systemnotice.service.SystemNoticeService;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

@Component
@DisallowConcurrentExecution
public class IssueSystemNoticeJob extends JobBase {

    @Resource
    private SystemNoticeMapper systemNoticeMapper;

    @Resource
    private SystemNoticeService systemNoticeService;

    @Override
    public String getGroupName() {
        return TenantContext.get().getTenantUuid() + "-ISSUE-SYSTEMNOTICE";
    }

    @Override
    public void reloadJob(JobObject jobObject) {
        String tenantUuid = jobObject.getTenantUuid();
        TenantContext.get().switchTenant(tenantUuid);
        Long noticeId = Long.valueOf(jobObject.getJobName());
        SystemNoticeVo systemNotice = systemNoticeMapper.getSystemNoticeById(noticeId);
        if (systemNotice != null && systemNotice.getStartTime() != null
                && (Objects.equals(systemNotice.getStatus(), SystemNoticeVo.Status.NOTISSUED.getValue()) || Objects.equals(systemNotice.getStatus(), SystemNoticeVo.Status.STOPPED.getValue()))) {
            JobObject.Builder newJobObjectBuilder = new JobObject.Builder(noticeId.toString(), this.getGroupName(), this.getClassName(), TenantContext.get().getTenantUuid())
                    .withBeginTime(systemNotice.getStartTime())
                    .withIntervalInSeconds(60 * 60)
                    .withRepeatCount(0);
            JobObject newJobObject = newJobObjectBuilder.build();
            schedulerManager.loadJob(newJobObject);
        }
    }

    @Override
    public void initJob(String tenantUuid) {
        List<Long> noticeIdList = systemNoticeMapper.getNotIssuedNoticeIdList();
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
        if (systemNotice != null && systemNotice.getStartTime() != null
                && (Objects.equals(systemNotice.getStatus(), SystemNoticeVo.Status.NOTISSUED.getValue()) || Objects.equals(systemNotice.getStatus(), SystemNoticeVo.Status.STOPPED.getValue()))) {
            return true;
        }
        return false;
    }

    @Override
    public void executeInternal(JobExecutionContext context, JobObject jobObject) throws Exception {
        Long noticeId = Long.valueOf(jobObject.getJobName());
        SystemNoticeVo systemNotice = systemNoticeMapper.getSystemNoticeById(noticeId);
        if (systemNotice != null && systemNotice.getStartTime() != null
                && (Objects.equals(systemNotice.getStatus(), SystemNoticeVo.Status.NOTISSUED.getValue()) || Objects.equals(systemNotice.getStatus(), SystemNoticeVo.Status.STOPPED.getValue()))) {
            systemNoticeService.issueSystemNotice(systemNotice);
            systemNotice.setStatus(SystemNoticeVo.Status.ISSUED.getValue());
            systemNotice.setIssueTime(systemNotice.getStartTime());
            systemNoticeMapper.updateSystemNoticeIssueInfo(systemNotice);
        }
    }
}
