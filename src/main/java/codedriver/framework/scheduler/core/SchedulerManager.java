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
import codedriver.framework.common.util.ModuleUtil;
import codedriver.framework.common.util.SerializerUtil;
import codedriver.framework.dao.mapper.DatasourceMapper;
import codedriver.framework.dto.DatasourceVo;
import codedriver.framework.dto.ModuleVo;
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
	private DatasourceMapper datasourceMapper;
	@Autowired 
	private ServerMapper serverMapper;
	@Autowired
	private DataSourceTransactionManager dataSourceTransactionManager;
	
	private List<DatasourceVo> datasourceList = new ArrayList<>();
	
	@PostConstruct
	public final void init() {
		datasourceList = datasourceMapper.getAllDatasource();
		//定时检查有没有新的定时作业要加载
		ScheduledExecutorService newJobService = Executors.newScheduledThreadPool(1);
		CodeDriverThread newJobRunnable = new CodeDriverThread() {
			@Override
			protected void execute() {
				String oldThreadName = Thread.currentThread().getName();
				try {
					Thread.currentThread().setName("NEW_JOB_CHECK");
					if(tenantContext == null) {
						tenantContext = TenantContext.init();
					}						
					tenantContext.setUseDefaultDatasource(true);
					List<ServerNewJobVo> newJobList = schedulerMapper.getServerJobByServerId(Config.SCHEDULE_SERVER_ID);
					for(ServerNewJobVo newJob : newJobList) {
						tenantContext.setUseDefaultDatasource(true);
						schedulerMapper.deleteServerJobById(newJob.getId());
						JobObject jobObject = (JobObject) SerializerUtil.getObjectByByteArray(newJob.getJobObject());
						if(jobObject != null) {
							loadJob(jobObject);
						}					
					}
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
	/**
	 * 
	* @Description: 加载定时作业，同时设置定时作业状态和锁 
	* @param jobObject 
	* @return void
	 */
	@SuppressWarnings({"rawtypes","unchecked"})
	public void loadJob(JobObject jobObject) {
		//如果结束时间比当前时间早，就不加载了
		if (jobObject.getEndTime() != null && jobObject.getEndTime().before(new Date())) {
			return;
		}
		try {
			String jobId = jobObject.getJobId();
			String groupName = jobObject.getJobGroup();
			//获取租户uuid，用于切换数据源
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
			// 开始事务
			DefaultTransactionDefinition def = new DefaultTransactionDefinition();
			def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
			TransactionStatus transactionStatus = dataSourceTransactionManager.getTransaction(def);
			try {
				JobStatusVo jobStatus = schedulerMapper.getJobStatusByJobUuid(jobId);
				if(jobStatus != null ) {//将设置为运行中
					jobStatus.setJobGroup(groupName);
					jobStatus.setStatus(JobStatusVo.RUNNING);
					jobStatus.setNeedAudit(jobObject.getNeedAudit());
					schedulerMapper.updateJobStatusByJobUuid(jobStatus);
				}else {
					//首次加载该定时作业时，添加执行状态
					jobStatus = new JobStatusVo(jobId, groupName, JobStatusVo.RUNNING, jobObject.getNeedAudit());
					schedulerMapper.insertJobStatus(jobStatus);
				}
				JobLockVo jobLock = schedulerMapper.getJobLockByUuid(jobId);
				if(jobLock == null) {
					//首次加载该定时作业时，添加锁
					schedulerMapper.insertJobLock(new JobLockVo(jobId, JobLockVo.WAIT, Config.SCHEDULE_SERVER_ID));
				}
				dataSourceTransactionManager.commit(transactionStatus);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				dataSourceTransactionManager.rollback(transactionStatus);
			}
			// 结束事务
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
	/**
	 * 
	* @Description: 新添加job时调用该方法通知其他服务器加载该job
	* @param jobObject 
	* @return void
	 */
	public void broadcastNewJob(JobObject jobObject) {
		byte[] jobObjectByteArray = SerializerUtil.getByteArrayByObject(jobObject);
		TenantContext.get().setUseDefaultDatasource(true);
		//获取在线服务器信息
		List<ServerClusterVo> serverList = serverMapper.getServerByStatus(ServerClusterVo.STARTUP);
		for(ServerClusterVo server : serverList) {
			int serverId = server.getServerId();
			if(Config.SCHEDULE_SERVER_ID == serverId) {
				continue;
			}
			schedulerMapper.insertServerJob(new ServerNewJobVo(serverId, jobObjectByteArray));
		}
		TenantContext.get().setUseDefaultDatasource(false);
	}
	/**
	 * 
	* @Description: 暂停定时作业，将定时作业从调度器中删除，同时将定时作业状态设置为停止
	* @param jobUuid 定时作业uuid
	* @return void
	 */
	public void pauseJob(String jobUuid) {
		try {
			JobStatusVo jobStatus = schedulerMapper.getJobStatusByJobUuid(jobUuid);
			if(jobStatus == null) {
				return;
			}
			//将定时作业状态设置为停止
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
	/**
	 * 
	* @Description: 删除定时作业，将定时作业从调度器中删除，同时删除该定时作业的执行状态和锁数据
	* @param jobUuid 定时作业uuid
	* @return void
	 */
	public void deleteJob(String jobUuid) {
		try {
			JobStatusVo jobStatus = schedulerMapper.getJobStatusByJobUuid(jobUuid);
			if(jobStatus == null) {
				return;
			}
			//删除该定时作业的执行状态和锁数据
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
	/**
	 * 
	* @Description: 根据定时作业组件类的全限定名，批量加载定时作业
	* @param classpath 
	* @return void
	 */
	private void loadJob(String classpath) {
		List<JobVo> jobList = schedulerMapper.getJobByClasspath(classpath);
		for (JobVo job : jobList) {
			JobObject jobObject = JobObject.buildJobObject(job, JobObject.FRAMEWORK);
			loadJob(jobObject);
		}
	}
	/**
	 * 
	* @Description: 某台服务器故障时，其他服务器调用该方法释放故障服务器占用的锁
	* @param serverId 故障服务器id
	* @return void
	 */
	public void releaseLock(Integer serverId) {
		JobLockVo jobLock = new JobLockVo(JobLockVo.WAIT, serverId);
		TenantContext tenantContext = TenantContext.get();
		if(tenantContext == null) {
			tenantContext = TenantContext.init();
		}
		for(DatasourceVo datasourceVo : datasourceList) {
			tenantContext.setTenantUuid(datasourceVo.getTenantUuid());
			tenantContext.setUseDefaultDatasource(false);
			schedulerMapper.updateJobLockByServerId(jobLock);
		}
	}
	
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		ApplicationContext context = event.getApplicationContext();
		TenantContext tenant = TenantContext.get();
		if(tenant == null) {
			tenant = TenantContext.init();
		}
		tenant.setUseDefaultDatasource(true);
		List<ModuleVo> moduleList = ModuleUtil.getAllModuleList();
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
			//如果定时作业组件没有实现IPublicJob接口，不会插入schedule_job_class表
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
