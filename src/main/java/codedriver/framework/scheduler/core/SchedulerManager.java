package codedriver.framework.scheduler.core;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.quartz.CronExpression;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import codedriver.framework.applicationlistener.core.ApplicationListenerBase;
import codedriver.framework.asynchronization.thread.CodeDriverThread;
import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.asynchronization.threadpool.CachedThreadPool;
import codedriver.framework.common.RootComponent;
import codedriver.framework.dto.TenantVo;
import codedriver.framework.scheduler.dao.mapper.SchedulerMapper;
import codedriver.framework.scheduler.dto.JobClassVo;
import codedriver.framework.scheduler.dto.JobLockVo;
import codedriver.framework.scheduler.dto.JobObject;
import codedriver.framework.scheduler.dto.JobStatusVo;

@RootComponent
public class SchedulerManager extends ApplicationListenerBase {
	private Logger logger = LoggerFactory.getLogger(SchedulerManager.class);

	private static Map<String, IJob> jobHandlerMap = new HashMap<>();
	private static Map<String, JobClassVo> jobClassMap = new HashMap<>();
	private static List<JobClassVo> publicJobClassList = new ArrayList<>();

	@Autowired
	private SchedulerFactoryBean schedulerFactoryBean;
	@Autowired
	private SchedulerMapper schedulerMapper;

	private List<TenantVo> tenantList = new ArrayList<>();

	protected void myInit() {
		tenantList = tenantMapper.getAllActiveTenant();
	}

	public static IJob getHandler(String className) {
		return jobHandlerMap.get(className);
	}

	public static List<JobClassVo> getAllPublicJobClassList() {
		return publicJobClassList;
	}

	public static JobClassVo getJobClassByClasspath(String classpath) {
		JobClassVo jobClassVo = jobClassMap.get(classpath);
		if (jobClassVo != null && TenantContext.get().containsModule(jobClassVo.getModuleId())) {
			return jobClassVo;
		}
		return null;
	}

	public boolean checkJobIsExists(String jobName, String jobGroup) {
		JobKey jobKey = new JobKey(jobName, jobGroup);
		Scheduler scheduler = schedulerFactoryBean.getScheduler();
		try {
			if (scheduler.getJobDetail(jobKey) != null) {
				return true;
			}
		} catch (SchedulerException e) {
			logger.error(e.getMessage(), e);
		}
		return false;
	}

	/**
	 * 
	 * @Description: 加载定时作业，同时设置定时作业状态和锁
	 * @param jobObject
	 * @return void
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void loadJob(JobObject jobObject) {
		// 如果结束时间比当前时间早，就不加载了
		if (jobObject.getEndTime() != null && jobObject.getEndTime().before(new Date())) {
			return;
		}
		try {
			String jobName = jobObject.getJobName();
			String jobGroup = jobObject.getJobGroup();
			String className = jobObject.getJobClassName();

			JobKey jobKey = new JobKey(jobName, jobGroup);
			Scheduler scheduler = schedulerFactoryBean.getScheduler();
			if (scheduler.getJobDetail(jobKey) != null) {
				scheduler.deleteJob(jobKey);
			}

			TriggerBuilder triggerBuilder = TriggerBuilder.newTrigger().withIdentity(jobName, jobGroup);

			if (CronExpression.isValidExpression(jobObject.getCron())) {
				triggerBuilder.withSchedule(CronScheduleBuilder.cronSchedule(jobObject.getCron()));
			} else {
				return;
			}
			try {
				JobLockVo jobLockVo = schedulerMapper.getJobLockByJobNameGroup(jobName, jobGroup);
				if (jobLockVo == null) {
					jobLockVo = new JobLockVo(jobName, jobGroup, className);
					schedulerMapper.insertJobLock(jobLockVo);
				}

				Date startTime = jobObject.getBeginTime();
				if (startTime != null && startTime.after(new Date())) {
					triggerBuilder.startAt(startTime);
				} else {
					triggerBuilder.startNow();
				}
				triggerBuilder.endAt(jobObject.getEndTime());
				Trigger trigger = triggerBuilder.build();
				Class clazz = Class.forName(jobObject.getJobClassName());
				JobDetail jobDetail = JobBuilder.newJob(clazz).withIdentity(jobKey).build();
				jobDetail.getJobDataMap().put("jobObject", jobObject);
				Date nextFireDate = scheduler.scheduleJob(jobDetail, trigger);
				// 写入jobstatus
				JobStatusVo jobStatusVo = schedulerMapper.getJobStatusByJobNameGroup(jobName, jobGroup);
				if (jobStatusVo == null) {
					jobStatusVo = new JobStatusVo();
					jobStatusVo.setJobName(jobName);
					jobStatusVo.setJobGroup(jobGroup);
					jobStatusVo.setClassName(className);
					jobStatusVo.setNextFireTime(nextFireDate);
					schedulerMapper.insertJobStatus(jobStatusVo);
				} else {
					jobStatusVo.setNextFireTime(nextFireDate);
					schedulerMapper.updateJobStatus(jobStatusVo);
				}
			} catch (Exception ex) {
				logger.error(ex.getMessage(), ex);
			}

		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
		}
	}

	/**
	 * 
	 * @Description: 将定时作业从调度器中删除
	 * @param jobObject
	 *            作业信息
	 * @return void
	 */
	public boolean unloadJob(JobObject jobObject) {
		try {
			Scheduler scheduler = schedulerFactoryBean.getScheduler();
			JobKey jobKey = new JobKey(jobObject.getJobName(), jobObject.getJobGroup());
			if (scheduler.getJobDetail(jobKey) != null) {
				scheduler.deleteJob(jobKey);
			}
			// 清除作业锁和作业状态信息
			schedulerMapper.deleteJobLock(jobObject.getJobName(), jobObject.getJobGroup());
			schedulerMapper.deleteJobStatus(jobObject.getJobName(), jobObject.getJobGroup());
			return true;
		} catch (SchedulerException e) {
			logger.error(e.getMessage(), e);
			return false;
		}
	}

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		ApplicationContext context = event.getApplicationContext();
		Map<String, IJob> myMap = context.getBeansOfType(IJob.class);
		List<IJob> tmpJobHandlerList = new ArrayList<>();
		for (Map.Entry<String, IJob> entry : myMap.entrySet()) {
			IJob job = entry.getValue();
			tmpJobHandlerList.add(job);
			jobHandlerMap.put(job.getClassName(), job);
			JobClassVo jobClassVo = new JobClassVo(job.getClassName(), context.getId());
			jobClassMap.put(job.getClassName(), jobClassVo);
			// 如果定时作业组件没有实现IPublicJob接口，不会插入schedule_job_class表
			if (job instanceof IPublicJob) {
				IPublicJob publicJob = (IPublicJob) job;
				jobClassVo.setName(publicJob.getName());
				jobClassVo.setType(JobClassVo.PUBLIC);
				publicJobClassList.add(jobClassVo);
			}
		}
		for (TenantVo tenantVo : tenantList) {
			CachedThreadPool.execute(new ScheduleLoadJobRunner(tenantVo.getUuid(), tmpJobHandlerList));
		}
		// TODO 这里要增加清理job_status的逻辑
	}

	class ScheduleLoadJobRunner extends CodeDriverThread {

		private String tenantUuid;
		private List<IJob> jobHandlerList;

		public ScheduleLoadJobRunner(String _tenantUuid, List<IJob> _jobHandlerList) {
			tenantUuid = _tenantUuid;
			jobHandlerList = _jobHandlerList;
		}

		@Override
		protected void execute() {
			String oldThreadName = Thread.currentThread().getName();
			try {
				Thread.currentThread().setName("SCHEDULE-JOB-LOADER-" + tenantUuid);
				// 切换租户数据源
				TenantContext.get().switchTenant(tenantUuid).setUseDefaultDatasource(false);
				for (IJob jobHandler : jobHandlerList) {
					jobHandler.initJob(tenantUuid);
				}
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			} finally {
				Thread.currentThread().setName(oldThreadName);
			}
		}
	}

}
