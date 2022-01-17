package codedriver.module.framework.scheduler;

import codedriver.framework.dao.mapper.LoginMapper;
import codedriver.framework.scheduler.core.PublicJobBase;
import codedriver.framework.scheduler.dao.mapper.SchedulerMapper;
import codedriver.framework.scheduler.dto.JobObject;
import codedriver.framework.scheduler.dto.JobVo;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author longrf
 * @date 2022/1/17 3:11 下午
 */

@Service
@DisallowConcurrentExecution
public class PurgeLoginCaptchaDataSchedule extends PublicJobBase {

    @Resource
    SchedulerMapper schedulerMapper;

    @Resource
    LoginMapper loginMapper;

    @Override
    public String getName() {
        return "清除登陆验证码数据";
    }

    @Override
    public void executeInternal(JobExecutionContext context, JobObject jobObject) throws Exception {
        JobDetail jobDetail = context.getJobDetail();
        String jobUuid = jobDetail.getKey().getName();
        JobVo jobVo = schedulerMapper.getJobByUuid(jobUuid);
        if (jobVo != null) {
            //删除无效的（过期的）登陆验证码
            loginMapper.deleteLoginInvalidCaptcha();
        }
    }
}
