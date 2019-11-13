package codedriver.framework.scheduler;

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

import codedriver.framework.common.RootComponent;
import codedriver.framework.common.config.Config;
import codedriver.framework.dao.mapper.ModuleMapper;
import codedriver.framework.dao.mapper.ScheduleMapper;
import codedriver.framework.dto.ModuleVo;
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

	@PostConstruct
	public final void init() {
		//将任务状态为1改为-2,0改为-3
		scheduleMapper.resetJobStatusNotStart(Config.SCHEDULE_SERVER_ID);
	}

	public static IJob getInstance(String jobClassName){
		return iJobMap.get(jobClassName);
	}

	@SuppressWarnings("rawtypes")
	public void loadJob(JobObject jobObject) {
		if (jobObject.getEndTime() != null && jobObject.getEndTime().before(new Date())) {
			return;
		}
		Scheduler scheduler = schedulerFactoryBean.getScheduler();
		JobVo jobVo = scheduleMapper.getJobById(jobObject.getJobId());
		IJob job = iJobMap.get(jobObject.getJobClassName());
		try {
			if (jobVo != null && job != null){
				if (!job.valid(jobVo.getPropList())){
					return;
				}
			}
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
				if (jobObject.getRepeat() != null && jobObject.getRepeat() > 1) { 
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
			try {
				iJobMap.put(jobClass.getClassName(), jobClass);
				int id = jobClass.getJobClassId();
				String name = jobClass.getJobClassName();
				String classPath = jobClass.getClassName();
				
				jobClassVo = new JobClassVo();
				jobClassVo.setId(id);
				jobClassVo.setName(name);
				jobClassVo.setClassPath(classPath);
				jobClassVo.setModuleName(moduleName);

				if ((scheduleMapper.getJobClassVoCount(jobClassVo)) > 0) {
					scheduleMapper.updateJobClass(jobClassVo);
				} else {
					jobClassVo.setType(0);
					scheduleMapper.insertJobClass(jobClassVo);
				}

				List<JobVo> jobList = scheduleMapper.getJobByClassId(jobClass.getJobClassId(), Config.SCHEDULE_SERVER_ID);
				if (jobList != null && jobList.size() > 0) {
					loadJob(jobList);
				}
			} catch (ClassNotFoundException | SchedulerException e) {
				logger.error(e.getMessage(), e);
			}
		}
	}

	protected void loadJob(List<JobVo> jobList) throws SchedulerException, ClassNotFoundException {		
		//执行状态为-2的还原为1，并重新加入到任务计划中;-3的还原为0(最后没有加载的模块或者类下的job的状态可能为-3，-2)
		for (JobVo job : jobList) {
			if (job.getStatus().equals(-2)) {
				JobObject jobObject = JobObject.buildJobObject(job);
				loadJob(jobObject);
				job.setStatus(1);
			} else if (job.getStatus().equals(-3)) {
				job.setStatus(0);				
			}
			scheduleMapper.updateJobStatus(job);
		}
	}

}
