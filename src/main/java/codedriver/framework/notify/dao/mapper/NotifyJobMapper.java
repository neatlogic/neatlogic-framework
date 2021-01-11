package codedriver.framework.notify.dao.mapper;

import codedriver.framework.notify.dto.job.NotifyJobAuditVo;
import codedriver.framework.notify.dto.job.NotifyJobReceiverVo;
import codedriver.framework.notify.dto.job.NotifyJobVo;
import codedriver.framework.scheduler.dto.JobAuditVo;

import java.util.List;

public interface NotifyJobMapper {

    public int checkNameIsRepeat(NotifyJobVo vo);

    public NotifyJobVo getJobBaseInfoById(Long id);

    public NotifyJobVo getJobDetailById(Long id);

    public int searchJobCount(NotifyJobVo vo);

    public List<NotifyJobVo> searchJob(NotifyJobVo vo);

    public List<NotifyJobReceiverVo> getToListByJobId(Long id);

    public List<NotifyJobVo> getAllActiveJob();

    public List<NotifyJobAuditVo> searchJobAudit(JobAuditVo vo);

    public int updateJob(NotifyJobVo vo);

    public int updateJobStatus(NotifyJobVo vo);

    public int insertJob(NotifyJobVo vo);

    public int batchInsertReceiver(List<NotifyJobReceiverVo> list);

    public int deleteJobById(Long id);

    public int deleteReceiverByJobId(Long jobId);

}
