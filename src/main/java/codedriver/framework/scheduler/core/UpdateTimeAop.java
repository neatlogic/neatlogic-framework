package codedriver.framework.scheduler.core;

import java.util.Date;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;

import codedriver.framework.common.RootComponent;
import codedriver.framework.scheduler.dao.mapper.ScheduleMapper;
import codedriver.framework.scheduler.dto.JobVo;

@RootComponent
@Aspect
public class UpdateTimeAop {
	@Autowired
	private ScheduleMapper scheduleMapper;
	
	@After("execution(* codedriver.framework.scheduler.IJob.executeInternal(..))")  
    public void after(JoinPoint joinPoint) {
        updateJobTime((JobExecutionContext)joinPoint.getArgs()[0]);
    }
	
	private void updateJobTime(JobExecutionContext jobContext) {
		JobVo schedule = new JobVo();
		schedule.setLastFinishTime(new Date());
		schedule.setLastFireTime(jobContext.getFireTime());
		schedule.setNextFireTime(jobContext.getNextFireTime());
		schedule.setId(Long.parseLong(jobContext.getJobDetail().getKey().getName()));
		scheduleMapper.updateScheduleJobTimeById(schedule);
	}
}
