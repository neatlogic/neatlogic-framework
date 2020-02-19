package codedriver.framework.scheduler.core;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.springframework.stereotype.Service;

import codedriver.framework.common.config.Config;
import codedriver.framework.scheduler.annotation.Input;
import codedriver.framework.scheduler.annotation.Param;
import codedriver.framework.scheduler.dto.JobObject;
@Service
@DisallowConcurrentExecution
public class TestPublicJob extends PublicJobBase {
	@Input({
		@Param(name="p_1", dataType="int", controlType="t1", controlValue="v1", description="p1", required=true),
		@Param(name="p_2", dataType="Integer", controlType="t2", controlValue="v2", description="p2", required=true),
		@Param(name="p_3", dataType="long", controlType="t3", controlValue="v3", description="p3", required=true),
		@Param(name="p_4", dataType="Long", controlType="t4", controlValue="v4", description="p4", required=true),
		@Param(name="p_5", dataType="String", controlValue="v5", description="p5", required=true)
		})
	@Override
	public void executeInternal(JobExecutionContext context, JobObject jobObject) throws JobExecutionException {
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
