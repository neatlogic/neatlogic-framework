package neatlogic.module.framework.scheduler;

import neatlogic.framework.scheduler.core.PublicJobBase;
import neatlogic.framework.scheduler.dto.JobObject;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

@Service
@DisallowConcurrentExecution
public class TestPublicSchedule extends PublicJobBase {
    @Override
    public String getName() {
        return "TEST";
    }

    @Override
    public void executeInternal(JobExecutionContext context, JobObject jobObject) throws Exception {
//        String key = new SimpleDateFormat("yyyy-MM-dd HH:ss:mm.SSS").format(jobObject.getLoadTime());
//        System.out.println("测试：" + new SimpleDateFormat("yyyy-MM-dd HH:ss:mm.SSS").format(new Date()) + ">>>>" + key);
        System.out.println("测试：" + new SimpleDateFormat("yyyy-MM-dd HH:ss:mm.SSS").format(new Date()));
    }
}
