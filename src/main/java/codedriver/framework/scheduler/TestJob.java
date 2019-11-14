package codedriver.framework.scheduler;

import java.util.List;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;

import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.scheduler.annotation.Input;
import codedriver.framework.scheduler.annotation.Param;
import codedriver.framework.scheduler.dao.mapper.ScheduleMapper;
import codedriver.framework.scheduler.dto.JobPropVo;
import codedriver.framework.scheduler.dto.JobVo;

@Component("job-2801")
@DisallowConcurrentExecution
public class TestJob extends JobBase {//  implements IJob

	private Logger logger = LoggerFactory.getLogger(TestJob.class.getName());
	
	@Autowired
	private ScheduleMapper scheduleMapper;
	
	@Input({@Param(name="p_1", dataType="int", controlType="t1", controlValue="v1", description="p1", required=true),
		@Param(name="p_2", dataType="Integer", controlType="t2", controlValue="v2", description="p2", required=true),
		@Param(name="p_3", dataType="long", controlType="t3", controlValue="v3", description="p3", required=true),
		@Param(name="p_4", dataType="Long", controlType="t4", controlValue="v4", description="p4", required=true),
		@Param(name="p_5", dataType="String", controlValue="v5", description="p5", required=true)})
	@Override
	public void executeInternal(JobExecutionContext context) throws JobExecutionException {
		JobDetail jobDetail = context.getJobDetail();
		JobKey jobKey = jobDetail.getKey();
		System.out.println(jobKey.getGroup());
		System.out.println(TenantContext.get().getTenantUuid());
		JobDataMap jobDataMap = jobDetail.getJobDataMap();		
		System.out.println(jobDataMap.getString("p_1"));
		System.out.println(jobDataMap.getString("p_2"));
		System.out.println(jobDataMap.getString("p_3"));
		System.out.println(jobDataMap.getString("p_4"));
		System.out.println(jobDataMap.getString("p_5"));
		System.out.println("一分钟执行一次");
		String jobId = jobKey.getName();
		JobVo jobVo = scheduleMapper.getJobById(Long.valueOf(jobId));
		List<JobPropVo> propList = jobVo.getPropList();
		if(propList != null && !propList.isEmpty()) {
			for(JobPropVo prop : propList) {
				System.out.println(prop.getName() + ":" + prop.getValue());
			}
		}
		logger.info("一分钟执行一次");
	}

	@Override
	public Integer getJobClassId() {
		return 2801;
	}

	@Override
	public String getJobClassName() {
		return "测试Job";
	}

}
