/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.module.framework.scheduler;

import codedriver.framework.scheduler.core.JobBase;
import codedriver.framework.scheduler.core.SchedulerManager;
import codedriver.framework.scheduler.dto.JobObject;
import codedriver.framework.util.SnowflakeUtil;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

@Service
@DisallowConcurrentExecution
public class TestJob extends JobBase {

    @Autowired
    private SchedulerManager schedulerManager;

    @Override
    public void executeInternal(JobExecutionContext context, JobObject jobObject) throws JobExecutionException {
        long id = SnowflakeUtil.uniqueLong();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        System.out.println("job " + id + " start at " + sdf.format(new Date()));
        try {
            Thread.sleep(1800000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        JobDetail jobDetail = context.getJobDetail();
        JobKey jobKey = jobDetail.getKey();
        System.out.println("job " + id + " end at " + sdf.format(new Date()));
        //System.out.println(jobKey.getName() + "-" + jobKey.getGroup() + "-" + Config.SCHEDULE_SERVER_ID + "-" + sdf.format(new Date()));
        //throw new ApiRuntimeException("定时任务异常哈哈哈");
    }

    @Override
    public Boolean isHealthy(JobObject jobObject) {
        return true;
    }

    @Override
    public void reloadJob(JobObject jobObject) {
        // TODO Auto-generated method stub

    }

    @Override
    public void initJob(String tenantUuid) {
        /*JobObject jobObject = new JobObject.Builder("aaa", "bbbb", this.getClassName(), tenantUuid).withCron("1,11,21,31,41,51 * * * * ? ").build();
        schedulerManager.loadJob(jobObject);*/
    }

    @Override
    public String getGroupName() {
        return "TESTGROUP";
    }

}
