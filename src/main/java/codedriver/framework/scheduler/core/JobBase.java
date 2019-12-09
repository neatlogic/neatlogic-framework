package codedriver.framework.scheduler.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.common.config.Config;
import codedriver.framework.exception.core.FrameworkExceptionMessageBase;
import codedriver.framework.exception.core.IApiExceptionMessage;
import codedriver.framework.exception.type.CustomExceptionMessage;
import codedriver.framework.scheduler.annotation.Input;
import codedriver.framework.scheduler.annotation.Param;
import codedriver.framework.scheduler.dao.mapper.SchedulerMapper;
import codedriver.framework.scheduler.dto.JobAuditVo;
import codedriver.framework.scheduler.dto.JobLockVo;
import codedriver.framework.scheduler.dto.JobObject;
import codedriver.framework.scheduler.dto.JobPropVo;
import codedriver.framework.scheduler.dto.JobStatusVo;
import codedriver.framework.scheduler.dto.JobVo;
import codedriver.framework.scheduler.exception.SchedulerExceptionMessage;
import codedriver.framework.scheduler.service.SchedulerService;

/**
 * 定时任务处理模块基类，所新增的定时任务类必须继承此类，新Job类必须实现接口内的2个方法。
 */
public abstract class JobBase implements IJob {

	private Logger logger = LoggerFactory.getLogger(JobBase.class);
	protected static SchedulerMapper schedulerMapper;
		
	protected static SchedulerManager schedulerManager;
	
	protected static SchedulerService schedulerService;

	@Autowired
	public void setSchedulerMapper(SchedulerMapper schMapper) {
		schedulerMapper = schMapper;
	}

	@Autowired
	protected void setSchedulerManager(SchedulerManager schManager) {
		schedulerManager = schManager;
	}
	
	@Autowired
	protected void setSchedulerService(SchedulerService schService) {
		schedulerService = schService;
	}
    @Override
    public final void execute(JobExecutionContext context) throws JobExecutionException {    	
    	
        JobDetail jobDetail = context.getJobDetail();       
        JobKey jobKey = jobDetail.getKey();
        //从job组名中获取租户uuid,切换到租户的数据源
        String groupName = jobKey.getGroup();
        int index = groupName.indexOf(JobObject.DELIMITER);
        String tenantUuid = groupName.substring(0,index);
		TenantContext.init(tenantUuid).setUseDefaultDatasource(false);	
        String jobUuid = jobKey.getName();        
        
        //抢锁前 获取定时作业状态信息
        JobStatusVo lockBeforeJobStatus = schedulerMapper.getJobStatusByJobUuid(jobUuid);
        if(lockBeforeJobStatus == null) {
        	IApiExceptionMessage message = new FrameworkExceptionMessageBase(new SchedulerExceptionMessage(new CustomExceptionMessage("定时作业："+ jobUuid + " 不存在")));
			logger.error(message.getErrorCode() + "-" + message.getError());
        	schedulerManager.pauseJob(jobUuid);
            return;
        }
        // 如果定时作业状态不是运行中，就停止定时调度
        if (!JobStatusVo.RUNNING.equals(lockBeforeJobStatus.getStatus())) {
        	schedulerManager.pauseJob(jobUuid);
            return;
        }
		
		Date fireTime = context.getFireTime();//本次执行激活时间
		Date nextFireTime = lockBeforeJobStatus.getNextFireTime();//数据库中记录的下次激活时间
		//如果数据库中记录的下次激活时间在本次执行激活时间之后，则放弃执行业务逻辑
    	if(nextFireTime != null && nextFireTime.after(fireTime)) {
    		return;
    	}
    	//抢不到锁，则放弃执行业务逻辑       		
		if(!schedulerService.getJobLock(jobUuid)) {
			return;
		}
		try {
			//抢到锁后，再次获取定时作业状态信息
			JobStatusVo lockAfterJobStatus = schedulerMapper.getJobStatusByJobUuid(jobUuid);
			//如果抢锁前后获取到的执行次数不相等，则放弃执行业务逻辑
			if(!lockBeforeJobStatus.getExecCount().equals(lockAfterJobStatus.getExecCount())) {
				return;
			}
			IJob job = SchedulerManager.getInstance(this.getClassName());
	        if (job == null) {
	        	return;
	        }
			if (JobVo.YES.equals(lockBeforeJobStatus.getNeedAudit())) {

	            JobAuditVo auditVo = new JobAuditVo(jobUuid, Config.SCHEDULE_SERVER_ID);
	            schedulerMapper.insertJobAudit(auditVo);
//	            String path = getFilePath();
//	            File logFile = new File(path + auditVo.getId() + ".log");
//	            auditVo.setLogPath(logFile.getPath());
//	            OutputStreamWriter logOut = null;

	            try {
//	                if (!logFile.getParentFile().exists()) {
//	                    logFile.getParentFile().mkdirs();
//	                }
//	                if (!logFile.exists()) {
//	                    logFile.createNewFile();
//	                }
//	                logOut = new OutputStreamWriter(new FileOutputStream(logFile, true), "UTF-8");
//	                if (logOut != null) {
//	                    context.put("logOutput", logOut);
//	                }

	                job.executeInternal(context);                   
	                auditVo.setState(JobAuditVo.SUCCESS);                    
	            } catch (Exception ex) {
//	                try {
	                    auditVo.setState(JobAuditVo.ERROR);
	                    logger.error(ex.getMessage(), ex);
//	                    logOut.write(ExceptionUtils.getStackTrace(ex));
//	                } catch (IOException e) {
//	                    logger.error(e.getMessage(), e);
//	                }
	            } finally {
	                schedulerMapper.updateJobAudit(auditVo);
//	                if (logOut != null) {
//	                    try {
//	                        logOut.flush();
//	                        logOut.close();
//	                    } catch (IOException e) {
//	                        e.printStackTrace();
//	                    }
//	                }
	            }
	        }else {
	        	job.executeInternal(context);
	        }
			//执行完业务逻辑后，更新定时作业状态信息
	        JobStatusVo jobStatus = new JobStatusVo();
	        jobStatus.setLastFinishTime(new Date());
	        jobStatus.setLastFireTime(fireTime);
			
			if(context.getNextFireTime() != null) {
				jobStatus.setNextFireTime(context.getNextFireTime());
			}else {				
				jobStatus.setStatus(JobStatusVo.STOP);
				schedulerManager.pauseJob(jobUuid);
			} 			   		
			jobStatus.setJobUuid(jobUuid);
			jobStatus.setExecCount(lockBeforeJobStatus.getExecCount()+1);
			
			schedulerMapper.updateJobStatusByJobUuid(jobStatus);
		}finally {
			schedulerMapper.updateJobLockByJobId(new JobLockVo(jobUuid, JobLockVo.WAIT));
		}
    }

	/**
	 * 主要定时方法实现区
	 */
	@Override
	public abstract void executeInternal(JobExecutionContext context) throws JobExecutionException;

	/**
	 * 获取类名
	 */
	@Override
	public String getClassName() {
		return this.getClass().getName();
	}

//    private String getFilePath() {
//        Calendar calendar = Calendar.getInstance();
//        String path = Config.SCHEDULE_AUDIT_PATH + File.separator + calendar.get(Calendar.YEAR) + File.separator + (calendar.get(Calendar.MONTH) +1)+ File.separator + calendar.get(Calendar.DAY_OF_MONTH) + File.separator;
//        path = path.replace(Config.TENANT_UUID,TenantContext.get().getTenantUuid());
//        return path;
//    }

    @Override
    public boolean valid(List<JobPropVo> propVoList) {
	    Map<String, Param> paramMap = initProp();
        if (paramMap.isEmpty()){
        	return true;
        }
        if (propVoList != null && propVoList.size() > 0){
	        for (JobPropVo jobProp : propVoList){       	
	            if (jobProp.getValue() == null || "".equals(jobProp.getValue())){
	            	continue;
	            }
	            Param param = paramMap.get(jobProp.getName());
	            if(param == null) {
	            	continue;
	            }
	            String dataType = param.dataType();
	            if("int".equals(dataType) || "Integer".equals(dataType)) {
	            	try {
	            		Integer.parseInt(jobProp.getValue());
	            	}catch(NumberFormatException e) {
	            		logger.error(e.getMessage(), e);
	            		throw new NumberFormatException("定时作业参数类型不匹配，参数" + jobProp.getName() + "的类型是" + dataType);
	            	}            	
	            }else if("long".equals(dataType) || "Long".equals(dataType)) {
	            	try {
	            		Long.parseLong(jobProp.getValue());
	            	}catch(NumberFormatException e) {
	            		logger.error(e.getMessage(), e);
	            		throw new NumberFormatException("定时作业参数类型不匹配，参数" + jobProp.getName() + "的类型是" + dataType);
	            	}           	
	            }else if("double".equals(dataType) || "Double".equals(dataType)) {
	            	try {
	            		Double.parseDouble(dataType);
	            	}catch(NumberFormatException e) {
	            		logger.error(e.getMessage(), e);
	            		throw new NumberFormatException("定时作业参数类型不匹配，参数" + jobProp.getName() + "的类型是" + dataType);
	            	}           	
	            }
	            paramMap.remove(jobProp.getName(), param);
	        }
        }
        // 检查是否有必传参数没传
        for(Entry<String, Param> entry : paramMap.entrySet()) {
        	Param param = entry.getValue();
        	if(param.required() == true) {
        		throw new RuntimeException("jobClass为" + this.getClassName()+","+param.name()+"是必填参数");
        	}
        }
        return true;
    }

    @Override
    public Map<String, Param> initProp() {
    	Map<String, Param> paramMap = new HashMap<>();
        try {
            Method method = this.getClass().getDeclaredMethod("executeInternal", JobExecutionContext.class);
            if (method == null || !method.isAnnotationPresent(Input.class)){
            	return paramMap;
            }
            for (Annotation anno : method.getDeclaredAnnotations()){
                if (anno.annotationType().equals(Input.class)){
                    Input input = (Input) anno;
                    Param[] params = input.value();
                    for (Param param: params){
                    	paramMap.put(param.name(),param);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return paramMap;
    }
}
