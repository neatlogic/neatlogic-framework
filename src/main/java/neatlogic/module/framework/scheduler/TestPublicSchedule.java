package neatlogic.module.framework.scheduler;

import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.scheduler.annotation.Param;
import neatlogic.framework.scheduler.annotation.Prop;
import neatlogic.framework.scheduler.core.PublicJobBase;
import neatlogic.framework.scheduler.dto.JobObject;
import neatlogic.framework.scheduler.dto.JobVo;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
@DisallowConcurrentExecution
public class TestPublicSchedule extends PublicJobBase {
    @Override
    public String getName() {
        return "TEST";
    }

    @Prop({
            @Param(name = "a",controlType = "text", dataType = "string", description = "test")
    })

    @Transactional
    @Override
    public void executeInternal(JobExecutionContext context, JobObject jobObject) throws Exception {
        JobDetail jobDetail = context.getJobDetail();
        String jobUuid = jobDetail.getKey().getName();
        JobVo jobVo = schedulerMapper.getJobBaseInfoByUuid(jobUuid);
        System.out.println("jobVo = " + JSONObject.toJSONString(jobVo.getName()));
        System.out.println(jobObject.getProp("a"));
//        String key = new SimpleDateFormat("yyyy-MM-dd HH:ss:mm.SSS").format(jobObject.getLoadTime());
//        System.out.println("测试：" + new SimpleDateFormat("yyyy-MM-dd HH:ss:mm.SSS").format(new Date()) + ">>>>" + key);
        System.out.println("测试：" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()));
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            System.out.println("事务激活");
        }
        TimeUnit.MINUTES.sleep(2);
    }
}
