package codedriver.framework.scheduler.core;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.quartz.CronExpression;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.asynchronization.threadpool.CommonThreadPool;
import codedriver.framework.common.RootComponent;
import codedriver.framework.common.config.Config;
import codedriver.framework.dao.mapper.DatasourceMapper;
import codedriver.framework.dao.mapper.ModuleMapper;
import codedriver.framework.dto.DatasourceVo;
import codedriver.framework.dto.ModuleVo;
import codedriver.framework.dto.TenantVo;
import codedriver.framework.scheduler.annotation.Param;
import codedriver.framework.scheduler.dao.mapper.SchedulerMapper;
import codedriver.framework.scheduler.dto.JobClassVo;
import codedriver.framework.scheduler.dto.JobLockVo;
import codedriver.framework.scheduler.dto.JobObject;
import codedriver.framework.scheduler.dto.JobVo;
import codedriver.framework.scheduler.dto.ServerNewJobVo;

@RootComponent
public class SchedulerManager implements ApplicationListener<ContextRefreshedEvent> {
	Logger logger = LoggerFactory.getLogger(SchedulerManager.class);

	private static Map<String, IJob> iJobMap = new HashMap<>();

	@Autowired
	private SchedulerFactoryBean schedulerFactoryBean;
	@Autowired
	private SchedulerMapper schedulerMapper;
	@Autowired
	private ModuleMapper moduleMapper;
	@Autowired
	private DatasourceMapper datasourceMapper;
	
	private List<DatasourceVo> datasourceList = new ArrayList<>();
	
	@PostConstruct
	public final void init() {
		datasourceList = datasourceMapper.getAllDatasource();
		ScheduledExecutorService newJobService = Executors.newScheduledThreadPool(1);
		Runnable newJobRunnable = new Runnable() {

			@Override
			public void run() {
				List<ServerNewJobVo> newJobList = schedulerMapper.getNewJobByServerId(Config.SCHEDULE_SERVER_ID);
				for(ServerNewJobVo newJob : newJobList) {
					schedulerMapper.deleteServerNewJobById(newJob.getId());
					TenantContext tenantContext = TenantContext.init(newJob.getTenantUuid());
					tenantContext.setUseDefaultDatasource(false);
					JobVo jobVo = schedulerMapper.getJobById(newJob.getJobId());
					loadJob(jobVo);
				}				
			}
			
		};
		newJobService.scheduleWithFixedDelay(newJobRunnable, 1, Config.SERVER_HEARTBEAT_RATE, TimeUnit.MINUTES);
	}

	public static IJob getInstance(String className){
		return iJobMap.get(className);
	}

	public void loadJob(JobVo jobVo) {	
		JobObject jobObject = JobObject.buildJobObject(jobVo);
		if (jobObject.getEndTime() != null && jobObject.getEndTime().before(new Date())) {
			return;
		}
		IJob job = SchedulerManager.getInstance(jobObject.getJobClassName());
		try {
			if (jobVo != null && job != null){
				Map<String, Param> paramMap = job.initProp();
				if (!job.valid(jobVo.getPropList())){
					return;
				}
			}
			JobKey jobKey = new JobKey(jobObject.getJobId().toString(), jobObject.getJobGroup());
			Scheduler scheduler = schedulerFactoryBean.getScheduler();
			if (scheduler.getJobDetail(jobKey) != null) {
				scheduler.deleteJob(jobKey);
			}
			
			TriggerBuilder triggerBuilder = TriggerBuilder.newTrigger().withIdentity(jobObject.getJobId().toString(), jobObject.getJobGroup()).usingJobData(jobObject.getJobDataMap());
			if (JobVo.CRON_TRIGGER.equals(jobObject.getTriggerType())) {
				if(CronExpression.isValidExpression(jobObject.getCron())) {
					triggerBuilder.withSchedule(CronScheduleBuilder.cronSchedule(jobObject.getCron()));
				}else {
					return;
				}				
			} else if(JobVo.SIMPLE_TRIGGER.equals(jobObject.getTriggerType())){
				if(jobObject.getInterval() > 0 || jobObject.getRepeat() > 1) {
					SimpleScheduleBuilder ssb = SimpleScheduleBuilder.simpleSchedule();
					if (jobObject.getInterval() > 0) {						
						ssb = ssb.withIntervalInSeconds(jobObject.getInterval());//默认												
					}
					if (jobObject.getRepeat() > 0) { 
						ssb = ssb.withRepeatCount(jobObject.getRepeat() - 1); 
					} else {
						ssb = ssb.repeatForever();
					}
					triggerBuilder.withSchedule(ssb);
				}else {
					return;
				}				
			}else {
				return;
			}
			Date startTime = jobObject.getStartTime();
			if(startTime != null && startTime.after(new Date())) {
				triggerBuilder.startAt(startTime);
			}else {
				triggerBuilder.startNow();
			}
			triggerBuilder.endAt(jobObject.getEndTime());
			triggerBuilder.startAt(jobObject.getStartTime());
			triggerBuilder.endAt(jobObject.getEndTime());
			Trigger trigger = triggerBuilder.build();
			Class clazz = Class.forName(jobObject.getJobClassName());
			JobDetail jobDetail = JobBuilder.newJob(clazz).withIdentity(jobKey).usingJobData(jobObject.getJobDataMap()).build();
		    scheduler.scheduleJob(jobDetail, trigger);			
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
		}
	}
	
	public void deleteJob(Long jobId) {
		try {
			Scheduler scheduler = schedulerFactoryBean.getScheduler();
			JobKey jobKey = new JobKey(jobId.toString(), TenantContext.get().getTenantUuid());
			if (scheduler.getJobDetail(jobKey) != null) {
				scheduler.deleteJob(jobKey);
			}			
		} catch (SchedulerException e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	private void loadJob(String tenantUuid, JobClassVo jobClassVo) {
		TenantContext tenantContext = TenantContext.init(tenantUuid);
		tenantContext.setUseDefaultDatasource(false);
		List<JobVo> jobList = schedulerMapper.getJobByClasspath(jobClassVo.getClasspath());
		for (JobVo job : jobList) {
			loadJob(job);
		}
	}
	
	public void releaseLock(int serverId) {
		JobLockVo jobLock = new JobLockVo(JobLockVo.RELEASE_LOCK, serverId);
		TenantContext tenantContext = TenantContext.init();
		tenantContext.setUseDefaultDatasource(true);
		schedulerMapper.updateJobLockByServerId(jobLock);
		for(DatasourceVo datasourceVo : datasourceList) {
			tenantContext.setTenantUuid(datasourceVo.getTenantUuid());
			tenantContext.setUseDefaultDatasource(false);
			schedulerMapper.updateJobLockByServerId(jobLock);
		}
	}
	
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		ApplicationContext context = event.getApplicationContext();
		List<ModuleVo> moduleList = moduleMapper.getAllModuleList();
		String moduleName = null;
		for (ModuleVo vo : moduleList) {
			if (context.getDisplayName().indexOf(vo.getName()) > -1) {
				moduleName = vo.getName();
				break;
			}
		}
		if (moduleName == null) {
			return;
		}
		JobClassVo jobClassVo = null;
		Map<String, IJob> myMap = context.getBeansOfType(IJob.class);
		iJobMap.putAll(myMap);
		for (Map.Entry<String, IJob> entry : myMap.entrySet()) {
			IJob jobClass = entry.getValue();		
			jobClassVo = new JobClassVo();
			jobClassVo.setName(jobClass.getJobClassName());
			jobClassVo.setClasspath(jobClass.getClassName());
			jobClassVo.setModuleName(moduleName);			
			
			if (schedulerMapper.getJobClassByClasspath(jobClassVo) != null) {
				schedulerMapper.updateJobClass(jobClassVo);
			} else {
				jobClassVo.setType(jobClass.getType());
				schedulerMapper.insertJobClass(jobClassVo);
			}
			CommonThreadPool.execute(new ScheduleLoadJobRunner(TenantVo.DISABLE_UUID,jobClassVo));
			for(DatasourceVo datasourceVo : datasourceList) {
				CommonThreadPool.execute(new ScheduleLoadJobRunner(datasourceVo.getTenantUuid(),jobClassVo));
			}	
		}
	}

	class ScheduleLoadJobRunner implements Runnable {

		private String tenantUuid;
		private JobClassVo jobClassVo;
		public ScheduleLoadJobRunner(String _tenantUuid,JobClassVo _jobClassVo) {
			tenantUuid = _tenantUuid;
			jobClassVo = _jobClassVo;
		}
		@Override
		public void run() {
			loadJob(tenantUuid, jobClassVo);			
		}		
	}
}
