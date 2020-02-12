package codedriver.framework.scheduler.core;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import codedriver.framework.common.config.Config;
import codedriver.framework.scheduler.dto.JobObject;

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
		/*
		 * JobObject jobOject = new JobObject.Builder("aaa", "bbbb",
		 * this.getClassName(),
		 * tenantUuid).withCron("1,11,21,31,41,51 * * * * ?").build();
		 * schedulerManager.loadJob(jobOject);
		 */
	}

	@Override
	public String getGroupName() {
		return "TESTGROUP";
	}

}
