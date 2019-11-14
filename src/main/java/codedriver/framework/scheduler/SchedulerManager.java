package codedriver.framework.scheduler;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.quartz.impl.matchers.GroupMatcher;
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
import codedriver.framework.scheduler.dao.mapper.ScheduleMapper;
import codedriver.framework.scheduler.dto.JobClassVo;
import codedriver.framework.scheduler.dto.JobObject;
import codedriver.framework.scheduler.dto.JobVo;

@RootComponent
public class SchedulerManager implements ApplicationListener<ContextRefreshedEvent> {
	Logger logger = LoggerFactory.getLogger(SchedulerManager.class);

	private static Map<String, IJob> iJobMap = new HashMap<>();

	@Autowired
	private SchedulerFactoryBean schedulerFactoryBean;
	@Autowired
	private ScheduleMapper scheduleMapper;
	@Autowired
	private ModuleMapper moduleMapper;

	@Autowired
	private DatasourceMapper datasourceMapper;
	
	private List<DatasourceVo> datasourceList = new ArrayList<>();
	
	@PostConstruct
	public final void init() {
		datasourceList = datasourceMapper.getAllDatasource();
	}

	public static IJob getInstance(String jobClassName){
		return iJobMap.get(jobClassName);
	}

	@SuppressWarnings("rawtypes")
	public void loadJob(JobObject jobObject) {
		if (jobObject.getEndTime() != null && jobObject.getEndTime().before(new Date())) {
			return;
		}
		if(jobObject.getCron() == null && jobObject.getRepeat() != null && jobObject.getRepeat() < 1) {
			return;
		}
		JobVo jobVo = scheduleMapper.getJobById(jobObject.getJobId());
		IJob job = iJobMap.get(jobObject.getJobClassName());
		try {
			if (jobVo != null && job != null){
				if (!job.valid(jobVo.getPropList())){
					return;
				}
			}
			Scheduler scheduler = schedulerFactoryBean.getScheduler();
			if (scheduler.getJobDetail(new JobKey(jobObject.getJobId().toString(), jobObject.getJobGroup())) != null) {
				scheduler.deleteJob(new JobKey(jobObject.getJobId().toString(), jobObject.getJobGroup()));
			}
			
			TriggerBuilder triggerBuilder = TriggerBuilder.newTrigger().withIdentity(jobObject.getJobId().toString(), jobObject.getJobGroup());
			if (jobObject.getCron() != null && CronExpression.isValidExpression(jobObject.getCron())) {
				triggerBuilder.withSchedule(CronScheduleBuilder.cronSchedule(jobObject.getCron()));
			} else if((jobObject.getInterval() != null && jobObject.getInterval() > 0) || (jobObject.getRepeat() != null && jobObject.getRepeat() > 1)){
				SimpleScheduleBuilder ssb = SimpleScheduleBuilder.simpleSchedule();
				if (jobObject.getInterval() != null && jobObject.getInterval() > 0) {						
					ssb = ssb.withIntervalInSeconds(jobObject.getInterval());//默认												
				}
				if (jobObject.getRepeat() != null && jobObject.getRepeat() > 0) { 
					ssb = ssb.withRepeatCount(jobObject.getRepeat() - 1); 
				} else {
					ssb = ssb.repeatForever();
				}
				triggerBuilder.withSchedule(ssb);
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
			JobDetail jobDetail = JobBuilder.newJob(clazz).withIdentity(jobObject.getJobId().toString(), jobObject.getJobGroup()).usingJobData(jobObject.getJobDataMap()).build();			
		     scheduler.scheduleJob(jobDetail, trigger);			
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
		}
	}

	public void pauseJob(JobObject jobObject) {
		try {
			Scheduler scheduler = schedulerFactoryBean.getScheduler();
			if (scheduler.getJobDetail(new JobKey(jobObject.getJobId().toString(), jobObject.getJobGroup())) != null) {
				scheduler.pauseJob(new JobKey(jobObject.getJobId().toString(), jobObject.getJobGroup()));
			}
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
		}
	}

	public void resumeJob(JobObject jobObject) {
		try {
			Scheduler scheduler = schedulerFactoryBean.getScheduler();
			if (scheduler.getJobDetail(new JobKey(jobObject.getJobId().toString(), jobObject.getJobGroup())) != null) {
				scheduler.resumeJob(new JobKey(jobObject.getJobId().toString(), jobObject.getJobGroup()));
			}
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
		}
	}

	public void deleteJob(JobObject jobObject) {
		Scheduler scheduler = schedulerFactoryBean.getScheduler();
		try {
			if (scheduler.getJobDetail(new JobKey(jobObject.getJobId().toString(), jobObject.getJobGroup())) != null) {
				scheduler.deleteJob(new JobKey(jobObject.getJobId().toString(), jobObject.getJobGroup()));
			}
		} catch (SchedulerException e) {
			logger.error(e.getMessage(), e);
		}
	}

	public void deleteJobByGroup(String groupName) {
		Scheduler scheduler = schedulerFactoryBean.getScheduler();
		try {
			for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
				if (scheduler.getJobDetail(jobKey) != null) {
					scheduler.deleteJob(jobKey);
				}
			}
		} catch (SchedulerException e) {
			logger.error(e.getMessage(), e);
		}
	}

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		TenantContext tenantContext = TenantContext.init();
		tenantContext.setUseDefaultDatasource(true);
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
		for (Map.Entry<String, IJob> entry : myMap.entrySet()) {
			IJob jobClass = entry.getValue();
			if (jobClass.getJobClassId() == null) {
				continue;
			}
			iJobMap.put(jobClass.getClassName(), jobClass);
			
			jobClassVo = new JobClassVo();
			jobClassVo.setId(jobClass.getJobClassId());
			jobClassVo.setName(jobClass.getJobClassName());
			jobClassVo.setClassPath(jobClass.getClassName());
			jobClassVo.setModuleName(moduleName);

			if ((scheduleMapper.getJobClassVoCount(jobClassVo)) > 0) {
				scheduleMapper.updateJobClass(jobClassVo);
			} else {
				jobClassVo.setType(0);
				scheduleMapper.insertJobClass(jobClassVo);
			}
			for(DatasourceVo datasourceVo : datasourceList) {
				CommonThreadPool.execute(new ScheduleLoadJobRunner(datasourceVo.getTenantUuid(),jobClassVo));
			}		
		}
	}

	protected void loadJob(String tenantUuid, JobClassVo jobClassVo) {
		TenantContext tenantContext = TenantContext.init(tenantUuid);
		tenantContext.setUseDefaultDatasource(false);
		List<JobVo> jobList = scheduleMapper.getJobByClassId(jobClassVo.getId(), Config.SCHEDULE_SERVER_ID);
		for (JobVo job : jobList) {
			job.setJobClass(jobClassVo);
			JobObject jobObject = JobObject.buildJobObject(job);
			loadJob(jobObject);
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
