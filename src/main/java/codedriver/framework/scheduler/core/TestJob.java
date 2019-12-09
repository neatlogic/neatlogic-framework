package codedriver.framework.scheduler.core;

import java.util.List;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.scheduler.annotation.Input;
import codedriver.framework.scheduler.annotation.Param;
import codedriver.framework.scheduler.dao.mapper.SchedulerMapper;
import codedriver.framework.scheduler.dto.JobPropVo;
import codedriver.framework.scheduler.dto.JobVo;

@DisallowConcurrentExecution
public class TestJob extends JobBase {

	private Logger logger = LoggerFactory.getLogger(TestJob.class);
	@Autowired
	private SchedulerMapper scheduleMapper;

	@Input({
			@Param(name = "p_1",
					dataType = "int",
					controlType = "t1",
					controlValue = "v1",
					description = "p1",
					required = true),
			@Param(name = "p_2",
					dataType = "Integer",
					controlType = "t2",
					controlValue = "v2",
					description = "p2",
					required = true),
			@Param(name = "p_3",
					dataType = "long",
					controlType = "t3",
					controlValue = "v3",
					description = "p3",
					required = true),
			@Param(name = "p_4",
					dataType = "Long",
					controlType = "t4",
					controlValue = "v4",
					description = "p4",
					required = true),
			@Param(name = "p_5",
					dataType = "String",
					controlValue = "v5",
					description = "p5",
					required = true) })
	@Override
	public void executeInternal(JobExecutionContext context) throws JobExecutionException {

		JobDetail jobDetail = context.getJobDetail();
		JobKey jobKey = jobDetail.getKey();
		logger.info(jobKey.getGroup());
		logger.info(TenantContext.get().getTenantUuid());
		String jobUuid = jobKey.getName();
		logger.info("TestJob一分钟执行一次:" + jobUuid);
		JobVo jobVo = scheduleMapper.getJobByUuid(jobUuid);

		List<JobPropVo> propList = jobVo.getPropList();
		if (propList != null && !propList.isEmpty()) {
			for (JobPropVo prop : propList) {
				logger.info(prop.getName() + ":" + prop.getValue());
			}
		}
		// logger.info("TestJob一分钟执行一次:" + jobUuid);

		// OutputStreamWriter logOut = (OutputStreamWriter)
		// context.get("logOutput");
		// try {
		// logOut.write("success");
		// } catch (IOException e) {
		// e.printStackTrace();
		// }

	}

}
