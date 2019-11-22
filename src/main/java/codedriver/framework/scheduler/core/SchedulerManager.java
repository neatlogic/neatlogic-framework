package codedriver.framework.scheduler.core;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
import codedriver.framework.dto.TenantVo;
import codedriver.framework.scheduler.annotation.Param;
import codedriver.framework.scheduler.dao.mapper.SchedulerMapper;
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
	private SchedulerMapper schedulerMapper;
	@Autowired
	private ModuleMapper moduleMapper;

	@Autowired
	private DatasourceMapper datasourceMapper;
	
	private List<DatasourceVo> datasourceList = new ArrayList<>();
	
	@PostConstruct
	public final void init() {
		datasourceList = datasourceMapper.getAllDatasource();
	}

	public static IJob getInstance(String className){
		return iJobMap.get(className);
	}

	@SuppressWarnings("rawtypes")
	public void loadJob(JobObject jobObject) {
		if (jobObject.getEndTime() != null && jobObject.getEndTime().before(new Date())) {
			return;
		}
		if(jobObject.getCron() == null && jobObject.getRepeat() != null && jobObject.getRepeat() < 1) {
			return;
		}
		JobVo jobVo = schedulerMapper.getJobById(jobObject.getJobId());
		IJob job = iJobMap.get(jobObject.getJobClassName());
		try {
			if (jobVo != null && job != null){
				Map<String, Param> paramMap = job.initProp();
				if (!job.valid(jobVo.getPropList())){
					return;
				}
			}
			Scheduler scheduler = schedulerFactoryBean.getScheduler();
			if (scheduler.getJobDetail(new JobKey(jobObject.getJobId().toString(), jobObject.getJobGroup())) != null) {
				scheduler.deleteJob(new JobKey(jobObject.getJobId().toString(), jobObject.getJobGroup()));
			}
			
			TriggerBuilder triggerBuilder = TriggerBuilder.newTrigger().withIdentity(jobObject.getJobId().toString(), jobObject.getJobGroup());
			if (JobVo.CRON_TRIGGER.equals(jobObject.getTriggerType())) {
				if(jobObject.getCron() != null && CronExpression.isValidExpression(jobObject.getCron())) {
					triggerBuilder.withSchedule(CronScheduleBuilder.cronSchedule(jobObject.getCron()));
				}else {
					return;
				}				
			} else if(JobVo.SIMPLE_TRIGGER.equals(jobObject.getTriggerType())){
				if((jobObject.getInterval() != null && jobObject.getInterval() > 0) || (jobObject.getRepeat() != null && jobObject.getRepeat() > 1)) {
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
			CommonThreadPool.execute(new ScheduleLoadJobRunner(TenantVo.DISABLE_UUID,jobClassVo, null));
			for(DatasourceVo datasourceVo : datasourceList) {
				CommonThreadPool.execute(new ScheduleLoadJobRunner(datasourceVo.getTenantUuid(),jobClassVo, null));
			}	
		}
	}

	protected void loadJob(String tenantUuid, JobClassVo jobClassVo, Integer oldScheduleServerId) {
		TenantContext tenantContext = TenantContext.init(tenantUuid);
		if(oldScheduleServerId != null) {
			schedulerMapper.updateServerId(jobClassVo.getClasspath(), Config.SCHEDULE_SERVER_ID, oldScheduleServerId);
		}
		List<JobVo> jobList = schedulerMapper.getJobByClasspath(jobClassVo.getClasspath(), Config.SCHEDULE_SERVER_ID);
		for (JobVo job : jobList) {
			job.setClasspath(jobClassVo.getClasspath());
			JobObject jobObject = JobObject.buildJobObject(job);
			loadJob(jobObject);
		}
	}

	/**
	 * 
	* @Description: TODO 接管其他服务器的job
	* @param @param oldScheduleServerId 旧服务器id
	* @return void
	 */
	protected void loadJob(Integer oldScheduleServerId) {
		JobClassVo jobClassVo = null;
		for(Entry<String, IJob> entry : iJobMap.entrySet()) {		
			IJob jobClass = entry.getValue();
			jobClassVo = new JobClassVo();
			jobClassVo.setName(jobClass.getJobClassName());
			jobClassVo.setClasspath(jobClass.getClassName());
			CommonThreadPool.execute(new ScheduleLoadJobRunner(TenantVo.DISABLE_UUID,jobClassVo, oldScheduleServerId));
			for(DatasourceVo datasourceVo : datasourceList) {
				CommonThreadPool.execute(new ScheduleLoadJobRunner(datasourceVo.getTenantUuid(),jobClassVo, oldScheduleServerId));
			}
		}
	}
	class ScheduleLoadJobRunner implements Runnable {

		private Integer oldScheduleServerId;
		private String tenantUuid;
		private JobClassVo jobClassVo;
		public ScheduleLoadJobRunner(String _tenantUuid,JobClassVo _jobClassVo, Integer _oldScheduleServerId) {
			tenantUuid = _tenantUuid;
			jobClassVo = _jobClassVo;
			oldScheduleServerId = _oldScheduleServerId;
		}
		@Override
		public void run() {
			loadJob(tenantUuid, jobClassVo, oldScheduleServerId);			
		}		
	}
}
