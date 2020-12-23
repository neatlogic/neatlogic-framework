package codedriver.framework.notify.dao.mapper;

import codedriver.framework.notify.dto.job.NotifyJobReceiverVo;
import codedriver.framework.notify.dto.job.NotifyJobVo;

import java.util.List;

public interface NotifyJobMapper {

    public int checkNameIsRepeat(NotifyJobVo vo);

    public NotifyJobVo getJobBaseInfoById(Long id);

    public int updateJob(NotifyJobVo vo);

    public int insertJob(NotifyJobVo vo);

    public int batchInsertReceiver(List<NotifyJobReceiverVo> list);

    public int deleteReceiverByJobId(Long jobId);

}
