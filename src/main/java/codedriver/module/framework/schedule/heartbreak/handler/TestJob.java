/*
 * Copyright (c)  2021 TechSure Co.,Ltd.  All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.module.framework.schedule.heartbreak.handler;

import codedriver.framework.common.config.Config;
import codedriver.framework.scheduler.core.JobBase;
import codedriver.framework.scheduler.core.SchedulerManager;
import codedriver.framework.scheduler.dto.JobObject;
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
		JobDetail jobDetail = context.getJobDetail();
		JobKey jobKey = jobDetail.getKey();
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		System.out.println(jobKey.getName() + "-" + jobKey.getGroup() + "-" + Config.SCHEDULE_SERVER_ID + "-" + sdf.format(new Date()));
	}

	@Override
	public Boolean checkCronIsExpired(JobObject jobObject) {
		return true;
	}

	@Override
	public void reloadJob(JobObject jobObject) {
		// TODO Auto-generated method stub

	}

	@Override
	public void initJob(String tenantUuid) {
		// JobObject jobOject = new JobObject.Builder("aaa", "bbbb",
		// this.getClassName(), tenantUuid).withCron("1,11,21,31,41,51 * * * *
		// ?").build();
		// schedulerManager.loadJob(jobOject);

	}

	@Override
	public String getGroupName() {
		return "TESTGROUP";
	}

}
