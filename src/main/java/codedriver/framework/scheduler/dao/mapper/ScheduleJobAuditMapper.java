package codedriver.framework.scheduler.dao.mapper;

import java.util.List;

import codedriver.framework.scheduler.dto.ScheduleJobAuditVo;

public interface ScheduleJobAuditMapper {

    public int insertScheduleJobAudit(ScheduleJobAuditVo scheduleJobAudit);

    public int updateScheduleJobAudit(ScheduleJobAuditVo scheduleJobAudit);
}
