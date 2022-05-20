/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.module.framework.auditconfig.handler;

import codedriver.framework.auditconfig.core.AuditCleanerBase;
import codedriver.framework.scheduler.dao.mapper.SchedulerMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class ScheduleAuditCleaner extends AuditCleanerBase {
    @Resource
    private SchedulerMapper schedulerMapper;

    @Override
    public String getName() {
        return "SCHEDULER-AUDIT";
    }

    @Override
    protected void myClean(int dayBefore) {
        schedulerMapper.deleteAuditByDayBefore(dayBefore);
    }
}
