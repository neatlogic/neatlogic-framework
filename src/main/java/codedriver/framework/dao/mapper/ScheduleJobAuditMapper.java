package codedriver.framework.dao.mapper;

import java.util.List;

import codedriver.framework.scheduler.dto.ScheduleJobAuditVo;

public interface ScheduleJobAuditMapper {

    public ScheduleJobAuditVo getScheduleJobAuditById(Long id);

    public int getScheduleJobAuditCount(ScheduleJobAuditVo scheduleJobAudit);

    public List<ScheduleJobAuditVo> getScheduleJobAuditByJobId(ScheduleJobAuditVo scheduleJobAudit);

    public int insertScheduleJobAudit(ScheduleJobAuditVo scheduleJobAudit);

    public int updateScheduleJobAudit(ScheduleJobAuditVo scheduleJobAudit);
}
