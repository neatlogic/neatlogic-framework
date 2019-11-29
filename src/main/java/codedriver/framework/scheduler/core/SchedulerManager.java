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
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import codedriver.framework.asynchronization.thread.CodeDriverThread;
import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.asynchronization.threadpool.CommonThreadPool;
import codedriver.framework.common.RootComponent;
import codedriver.framework.common.config.Config;
import codedriver.framework.common.util.SerializerUtil;
import codedriver.framework.dao.mapper.DatasourceMapper;
import codedriver.framework.dao.mapper.ModuleMapper;
import codedriver.framework.dto.DatasourceVo;
import codedriver.framework.dto.ModuleVo;
import codedriver.framework.scheduler.annotation.Param;
import codedriver.framework.scheduler.dao.mapper.SchedulerMapper;
import codedriver.framework.scheduler.dto.JobClassVo;
import codedriver.framework.scheduler.dto.JobLockVo;
import codedriver.framework.scheduler.dto.JobObject;
import codedriver.framework.scheduler.dto.JobStatusVo;
import codedriver.framework.scheduler.dto.JobVo;
import codedriver.framework.scheduler.dto.ServerNewJobVo;
import codedriver.framework.server.dao.mapper.ServerMapper;
import codedriver.framework.server.dto.ServerClusterVo;

@RootComponent
public class SchedulerManager implements ApplicationListener<ContextRefreshedEvent> {
	private Logger logger = LoggerFactory.getLogger(SchedulerManager.class);

	private static Map<String, IJob> iJobMap = new HashMap<>();

	@Autowired
	private SchedulerFactoryBean schedulerFactoryBean;
	@Autowired
	private SchedulerMapper schedulerMapper;
	@Autowired
	private ModuleMapper moduleMapper;
	@Autowired
	private DatasourceMapper datasourceMapper;
	@Autowired 
	private ServerMapper serverMapper;
	@Autowired
	private DataSourceTransactionManager dataSourceTransactionManager;
	
	private List<DatasourceVo> datasourceList = new ArrayList<>();
	
	@PostConstruct
	public final void init() {
		System.out.println("定时检查newJob启动");
		datasourceList = datasourceMapper.getAllDatasource();
		ScheduledExecutorService newJobService = Executors.newScheduledThreadPool(1);
		CodeDriverThread newJobRunnable = new CodeDriverThread() {
			@Override
			protected void execute() {
				String oldThreadName = Thread.currentThread().getName();
				try {
					Thread.currentThread().setName("NEW_JOB_CHECK");
					System.out.println("一次检查newJob开始");
					System.out.println(TenantContext.get());
					if(tenantContext == null) {
						tenantContext = TenantContext.init();
					}						
					tenantContext.setUseDefaultDatasource(true);
					List<ServerNewJobVo> newJobList = schedulerMapper.getNewJobByServerId(Config.SCHEDULE_SERVER_ID);
					for(ServerNewJobVo newJob : newJobList) {
						tenantContext.setUseDefaultDatasource(true);
						schedulerMapper.deleteServerNewJobById(newJob.getId());
						JobObject jobObject = (JobObject) SerializerUtil.getObjectByByteArray(newJob.getJobObject());
						if(jobObject != null) {
							loadJob(jobObject);
						}					
					}
					System.out.println("一次检查newJob结束");
				}catch(Exception e) {
					logger.error(e.getMessage(), e);
				}finally {
					Thread.currentThread().setName(oldThreadName);
				}			
			}
			
		};
		newJobService.scheduleWithFixedDelay(newJobRunnable, Config.SERVER_HEARTBEAT_RATE, Config.SERVER_HEARTBEAT_RATE, TimeUnit.SECONDS);
	}

	public static IJob getInstance(String className){
		return iJobMap.get(className);
	}

	public void loadJob(JobObject jobObject) {
		if (jobObject.getEndTime() != null && jobObject.getEndTime().before(new Date())) {
			return;
		}
//		IJob job = SchedulerManager.getInstance(jobObject.getJobClassName());
		try {
//			if (jobVo != null && job != null){
//				if (!job.valid(jobVo.getPropList())){
//					return;
//				}
//			}
			String jobId = jobObject.getJobId();
			String groupName = jobObject.getJobGroup();
			int index = groupName.indexOf(JobObject.DELIMITER);
	        String tenantUuid = groupName.substring(0,index);
			JobKey jobKey = new JobKey(jobId, groupName);
			Scheduler scheduler = schedulerFactoryBean.getScheduler();
			if (scheduler.getJobDetail(jobKey) != null) {
				scheduler.deleteJob(jobKey);
			}
			
			TriggerBuilder triggerBuilder = TriggerBuilder.newTrigger().withIdentity(jobId, groupName);
			
			if(CronExpression.isValidExpression(jobObject.getCron())) {
				triggerBuilder.withSchedule(CronScheduleBuilder.cronSchedule(jobObject.getCron()));
			}else {
				return;
			}
			TenantContext.get().setTenantUuid(tenantUuid);
			TenantContext.get().setUseDefaultDatasource(false);
			//TODO 开始事务
			DefaultTransactionDefinition def = new DefaultTransactionDefinition();
			def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
			TransactionStatus transactionStatus = dataSourceTransactionManager.getTransaction(def);
			try {
				JobStatusVo jobStatus = schedulerMapper.getJobStatusByJobUuid(jobId);
				if(jobStatus != null ) {
					jobStatus.setJobGroup(groupName);
					jobStatus.setStatus(JobStatusVo.RUNNING);
					jobStatus.setNeedAudit(jobObject.getNeedAudit());
					schedulerMapper.updateJobStatusByJobUuid(jobStatus);
				}else {
					jobStatus = new JobStatusVo(jobId, groupName, JobStatusVo.RUNNING, jobObject.getNeedAudit());
					schedulerMapper.insertJobStatus(jobStatus);
				}
				JobLockVo jobLock = schedulerMapper.getJobLockByUuid(jobId);
				if(jobLock == null) {
					schedulerMapper.insertJobLock(new JobLockVo(jobId, JobLockVo.WAIT, Config.SCHEDULE_SERVER_ID));
				}
				dataSourceTransactionManager.commit(transactionStatus);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				dataSourceTransactionManager.rollback(transactionStatus);
			}
			//TODO 结束事务
			Date startTime = jobObject.getStartTime();
			if(startTime != null && startTime.after(new Date())) {
				triggerBuilder.startAt(startTime);
			}else {
				triggerBuilder.startNow();
			}
			triggerBuilder.endAt(jobObject.getEndTime());
			Trigger trigger = triggerBuilder.build();
			Class clazz = Class.forName(jobObject.getJobClassName());
			JobDetail jobDetail = JobBuilder.newJob(clazz).withIdentity(jobKey).build();
		    scheduler.scheduleJob(jobDetail, trigger);		    
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
		}
	}
	
	public void broadcastNewJob(JobObject jobObject) {
		byte[] jobObjectByteArray = SerializerUtil.getByteArrayByObject(jobObject);
		TenantContext.get().setUseDefaultDatasource(true);
		List<ServerClusterVo> serverList = serverMapper.getServerByStatus(ServerClusterVo.STARTUP);
		for(ServerClusterVo server : serverList) {
			int serverId = server.getServerId();
			if(Config.SCHEDULE_SERVER_ID == serverId) {
				continue;
			}
			schedulerMapper.insertServerNewJob(new ServerNewJobVo(serverId, jobObjectByteArray));
		}
		TenantContext.get().setUseDefaultDatasource(false);
	}
	
	public void pauseJob(String jobUuid) {
		try {
//			TenantContext.get().setUseDefaultDatasource(false);
			JobStatusVo jobStatus = schedulerMapper.getJobStatusByJobUuid(jobUuid);
			if(jobStatus == null) {
				return;
			}
			schedulerMapper.updateJobStatusByJobUuid(new JobStatusVo(jobUuid, JobStatusVo.STOP));
			Scheduler scheduler = schedulerFactoryBean.getScheduler();
			JobKey jobKey = new JobKey(jobStatus.getJobUuid(), jobStatus.getJobGroup());
			if (scheduler.getJobDetail(jobKey) != null) {
				scheduler.deleteJob(jobKey);
			}			
		} catch (SchedulerException e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	public void deleteJob(String jobUuid) {
		try {
//			TenantContext.get().setUseDefaultDatasource(false);
			JobStatusVo jobStatus = schedulerMapper.getJobStatusByJobUuid(jobUuid);
			if(jobStatus == null) {
				return;
			}
			schedulerMapper.deleteJobStatusAndLockByJobUuid(jobUuid);
			Scheduler scheduler = schedulerFactoryBean.getScheduler();
			JobKey jobKey = new JobKey(jobStatus.getJobUuid(), jobStatus.getJobGroup());
			if (scheduler.getJobDetail(jobKey) != null) {
				scheduler.deleteJob(jobKey);
			}			
		} catch (SchedulerException e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	private void loadJob(String classpath) {
		List<JobVo> jobList = schedulerMapper.getJobByClasspath(classpath);
		for (JobVo job : jobList) {
			JobObject jobObject = JobObject.buildJobObject(job, JobObject.FRAMEWORK);
			loadJob(jobObject);
		}
	}
	
	public void releaseLock(Integer serverId) {
		JobLockVo jobLock = new JobLockVo(JobLockVo.WAIT, serverId);
		TenantContext tenantContext = TenantContext.get();
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
		TenantContext tenant = TenantContext.get();
		for (Map.Entry<String, IJob> entry : myMap.entrySet()) {
			IJob jobClass = entry.getValue();
			if(!(jobClass instanceof IPublicJob)) {
				continue;
			}
			IPublicJob publicJobClass = (IPublicJob) jobClass;
			jobClassVo = new JobClassVo();
			jobClassVo.setName(publicJobClass.getJobClassName());
			jobClassVo.setClasspath(publicJobClass.getClassName());
			jobClassVo.setModuleName(moduleName);
			tenant.setUseDefaultDatasource(true);
			if (schedulerMapper.getJobClassByClasspath(jobClassVo) != null) {
				schedulerMapper.updateJobClass(jobClassVo);
			} else {
				jobClassVo.setType(publicJobClass.getType());
				schedulerMapper.insertJobClass(jobClassVo);
			}
			for(DatasourceVo datasourceVo : datasourceList) {
				CommonThreadPool.execute(new ScheduleLoadJobRunner(datasourceVo.getTenantUuid(),publicJobClass.getClassName()));
			}	
		}
	}

	class ScheduleLoadJobRunner extends CodeDriverThread {

		private String tenantUuid;
		private String classpath;
		public ScheduleLoadJobRunner(String _tenantUuid,String _classpath) {
			tenantUuid = _tenantUuid;
			classpath = _classpath;
		}
		
		@Override
		protected void execute() {
			String oldThreadName = Thread.currentThread().getName();
			try {
				Thread.currentThread().setName("SCHEDULER_LOAD_JOB");
				System.out.println("SCHEDULER_LOAD_JOB:"+tenantContext);
				if(tenantContext == null) {
					tenantContext = TenantContext.init(tenantUuid);
				}else {
					tenantContext.setTenantUuid(tenantUuid);
				}
				tenantContext.setUseDefaultDatasource(false);				
				loadJob(classpath);
			}catch(Exception e) {
				logger.error(e.getMessage(), e);
			}finally {
				Thread.currentThread().setName(oldThreadName);
			}						
		}		
	}
}
