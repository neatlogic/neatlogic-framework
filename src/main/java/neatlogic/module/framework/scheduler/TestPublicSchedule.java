package neatlogic.module.framework.scheduler;

import neatlogic.framework.scheduler.annotation.Input;
import neatlogic.framework.scheduler.annotation.Param;
import neatlogic.framework.scheduler.core.PublicJobBase;
import neatlogic.framework.scheduler.dto.JobObject;
import neatlogic.framework.scheduler.dto.JobVo;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;

import java.text.SimpleDateFormat;
import java.util.Date;

@DisallowConcurrentExecution
public class TestPublicSchedule extends PublicJobBase {
    @Override
    public String getName() {
        return "TEST";
    }

    @Input({
            @Param(name = "a",controlType = "text", dataType = "string", description = "test")
    })
    @Override
    public void executeInternal(JobExecutionContext context, JobObject jobObject) throws Exception {
        JobDetail jobDetail = context.getJobDetail();
        String jobUuid = jobDetail.getKey().getName();
        JobVo jobVo = schedulerMapper.getJobByUuid(jobUuid);
        if (jobVo != null) {
            //用户入参
            jobVo.getPropList();
        }
//        String key = new SimpleDateFormat("yyyy-MM-dd HH:ss:mm.SSS").format(jobObject.getLoadTime());
//        System.out.println("测试：" + new SimpleDateFormat("yyyy-MM-dd HH:ss:mm.SSS").format(new Date()) + ">>>>" + key);
        System.out.println("测试：" + new SimpleDateFormat("yyyy-MM-dd HH:ss:mm.SSS").format(new Date()));
    }
}
