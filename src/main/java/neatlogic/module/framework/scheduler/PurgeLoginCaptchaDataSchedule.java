package neatlogic.module.framework.scheduler;

import neatlogic.framework.dao.mapper.LoginMapper;
import neatlogic.framework.scheduler.core.PublicJobBase;
import neatlogic.framework.scheduler.dao.mapper.SchedulerMapper;
import neatlogic.framework.scheduler.dto.JobObject;
import neatlogic.framework.scheduler.dto.JobVo;
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

    /** 返回登录验证码清理作业的语言 key，避免服务启动时固定显示语言。 */
    @Override
    public String getName() {
        return "framework.scheduler.job.purgelogincaptcha.name";
    }

    @Override
    public void executeInternal(JobExecutionContext context, JobObject jobObject) throws Exception {
        JobDetail jobDetail = context.getJobDetail();
        String jobUuid = jobDetail.getKey().getName();
        JobVo jobVo = schedulerMapper.getJobByUuid(jobUuid);
        if (jobVo != null) {
            //删除无效的（过期的）登录验证码
            loginMapper.deleteLoginInvalidCaptcha();
        }
    }
}
