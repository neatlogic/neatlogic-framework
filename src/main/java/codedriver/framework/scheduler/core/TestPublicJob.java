package codedriver.framework.scheduler.core;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import codedriver.framework.common.config.Config;
import codedriver.framework.scheduler.annotation.Input;
import codedriver.framework.scheduler.annotation.Param;
import codedriver.framework.scheduler.dao.mapper.SchedulerMapper;

@Service
@DisallowConcurrentExecution
public class TestPublicJob extends PublicJobBase {

	private Logger logger = LoggerFactory.getLogger(TestPublicJob.class.getName());

	@Autowired
	private SchedulerMapper scheduleMapper;

	@Override
	public void executeInternal(JobExecutionContext context) throws JobExecutionException {
		JobDetail jobDetail = context.getJobDetail();
		JobKey jobKey = jobDetail.getKey();
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		System.out.println(jobKey.getName() + "-" + jobKey.getGroup() + "-" + Config.SCHEDULE_SERVER_ID + "-" + sdf.format(new Date()));
	}

	@Override
	public String getName() {
		return "测试TestPublicJob";
	}

}
