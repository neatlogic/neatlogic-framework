package codedriver.framework.scheduler.core;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.common.config.Config;
import codedriver.framework.scheduler.annotation.Input;
import codedriver.framework.scheduler.annotation.Param;
import codedriver.framework.scheduler.dao.mapper.SchedulerMapper;
import codedriver.framework.scheduler.dto.JobPropVo;
import codedriver.framework.scheduler.dto.JobVo;
import codedriver.framework.scheduler.exception.SchedulerExceptionMessage;
import codedriver.framework.scheduler.service.SchedulerService;
import codedriver.framework.scheduler.dto.JobAuditVo;
import codedriver.framework.scheduler.dto.JobLockVo;

/**
 * 定时任务处理模块基类，所新增的定时任务类必须继承此类，新Job类必须实现接口内的2个方法。
 */
public abstract class JobBase implements IJob {

	private Logger logger = LoggerFactory.getLogger(JobBase.class);
	protected static SchedulerMapper schedulerMapper;
		
	protected static SchedulerService schedulerService;

	@Autowired
	public void setSchedulerMapper(SchedulerMapper schMapper) {
		schedulerMapper = schMapper;
	}

	@Autowired
	protected void setSchedulerService(SchedulerService schService) {
		schedulerService = schService;
	}
    @Override
    public final void execute(JobExecutionContext context) throws JobExecutionException {    	
    	
        JobDetail jobDetail = context.getJobDetail();       
        JobKey jobKey = jobDetail.getKey();
        String tenantUuid = jobKey.getGroup();
		TenantContext tenantContext = TenantContext.init(tenantUuid);
		tenantContext.setUseDefaultDatasource(false);
        String jobUuid = jobKey.getName();
        
        IJob job = SchedulerManager.getInstance(this.getClassName());
        if (job == null) {
        	SchedulerExceptionMessage message = new SchedulerExceptionMessage("定时作业组件："+ this.getClassName() + " 不存在");
			logger.error(message.toString());
        	System.out.println(message.toString());
        	schedulerService.stopJob(jobUuid);
            return;
        }
        JobVo getLockBeforeJob = schedulerMapper.getJobByUuid(jobUuid);
        if(getLockBeforeJob == null) {
        	SchedulerExceptionMessage message = new SchedulerExceptionMessage("定时作业："+ jobUuid + " 不存在");
			logger.error(message.toString());
        	schedulerService.stopJob(jobUuid);
            return;
        }
        if (!JobVo.RUNNING.equals(getLockBeforeJob.getStatus())) {
        	schedulerService.stopJob(jobUuid);
            return;
        }
        JobDataMap jobDataMap = jobDetail.getJobDataMap();
        int execCount = jobDataMap.getInt("execCount");
		System.err.println("jobDetail count: "+execCount);
		
		Date fireTime = context.getFireTime();
		Date nextFireTime = getLockBeforeJob.getNextFireTime();
    	if(execCount < getLockBeforeJob.getExecCount() || (nextFireTime != null && nextFireTime.after(fireTime))) {
    		jobDataMap.put("execCount",getLockBeforeJob.getExecCount());
    		return;
    	}
    	// 抢锁       		
		if(!schedulerService.getJobLock(jobUuid)) {
			jobDataMap.put("execCount",getLockBeforeJob.getExecCount() + 1);
			return;
		}
		try {
			JobVo getLockAfterJob = schedulerMapper.getJobByUuid(jobUuid);
			if(getLockAfterJob.getExecCount() > getLockBeforeJob.getExecCount()) {
				jobDataMap.put("execCount",getLockAfterJob.getExecCount());
				return;
			}
//			if (JobVo.YES.equals(jobVo.getNeedAudit())) {

//	            JobAuditVo auditVo = new JobAuditVo(jobId, Config.SCHEDULE_SERVER_ID);
//	            schedulerMapper.insertJobAudit(auditVo);
//	            String path = getFilePath();
//	            File logFile = new File(path + auditVo.getId() + ".log");
//	            auditVo.setLogPath(logFile.getPath());
//	            OutputStreamWriter logOut = null;
//
//	            try {
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
//
//	                job.executeInternal(context);                   
//	                auditVo.setState(JobAuditVo.SUCCESS);                    
//	            } catch (Exception ex) {
//	                try {
//	                    auditVo.setState(JobAuditVo.ERROR);
//	                    logger.error(ex.getMessage(), ex);
//	                    logOut.write(ExceptionUtils.getStackTrace(ex));
//	                } catch (IOException e) {
//	                    logger.error(e.getMessage(), e);
//	                }
//	            } finally {
//	                schedulerMapper.updateJobAudit(auditVo);
//	                if (logOut != null) {
//	                    try {
//	                        logOut.flush();
//	                        logOut.close();
//	                    } catch (IOException e) {
//	                        e.printStackTrace();
//	                    }
//	                }
//	            }
//	        }else {
//	        	job.executeInternal(context);
//	        }
			JobAuditVo auditVo = new JobAuditVo(jobUuid, Config.SCHEDULE_SERVER_ID);
            schedulerMapper.insertJobAudit(auditVo);
			job.executeInternal(context);
			auditVo.setState(JobAuditVo.SUCCESS);
			schedulerMapper.updateJobAudit(auditVo);
			jobDataMap.put("execCount",getLockBeforeJob.getExecCount() + 1);
	        JobVo schedule = new JobVo();
			schedule.setLastFinishTime(new Date());
			schedule.setLastFireTime(fireTime);
			Trigger trigger = context.getTrigger();
			if(trigger instanceof SimpleTrigger) {
				if(getLockBeforeJob.getRepeat() == getLockBeforeJob.getExecCount()+1) {
					schedule.setStatus(JobVo.STOP);
					schedulerService.stopJob(jobUuid);
				}else if(context.getNextFireTime() == null){
					getLockBeforeJob.setExecCount(getLockBeforeJob.getExecCount()+1);
					schedulerService.loadJob(getLockBeforeJob);
				}else {
					schedule.setNextFireTime(context.getNextFireTime());
				}
			}else {
				if(context.getNextFireTime() == null) {
					schedule.setStatus(JobVo.STOP);
					schedulerService.stopJob(jobUuid);
				}else {
					schedule.setNextFireTime(context.getNextFireTime());								
				} 
			}    		
			schedule.setUuid(jobUuid);
			schedule.setExecCount(getLockBeforeJob.getExecCount()+1);
			
			schedulerMapper.updateJobById(schedule);
		}finally {
			schedulerMapper.updateJobLockByJobId(new JobLockVo(jobUuid, JobLockVo.RELEASE_LOCK));
		}
    }

	/**
	 * 主要定时方法实现区
	 */
	@Override
	public abstract void executeInternal(JobExecutionContext context) throws JobExecutionException;
	@Override
	public abstract String getType();
	@Override
	public abstract String getJobClassName();

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
