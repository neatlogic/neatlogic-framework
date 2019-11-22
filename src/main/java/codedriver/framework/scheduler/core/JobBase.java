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
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.common.config.Config;
import codedriver.framework.scheduler.annotation.Input;
import codedriver.framework.scheduler.annotation.Param;
import codedriver.framework.scheduler.dao.mapper.SchedulerMapper;
import codedriver.framework.scheduler.dto.JobObject;
import codedriver.framework.scheduler.dto.JobPropVo;
import codedriver.framework.scheduler.dto.JobVo;
import codedriver.framework.scheduler.dto.JobAuditVo;

/**
 * 定时任务处理模块基类，所新增的定时任务类必须继承此类，新Job类必须实现接口内的2个方法。
 */
public abstract class JobBase implements IJob {

	Logger logger = LoggerFactory.getLogger(JobBase.class);
	protected static SchedulerMapper schedulerMapper;

	protected static SchedulerManager scheduleManager;

	@Autowired
	public void setScheduleMapper(SchedulerMapper schMapper) {
		schedulerMapper = schMapper;
	}

	@Autowired
	private void setScheduleManager(SchedulerManager schManager) {
		scheduleManager = schManager;
	}

    @Override
    public final void execute(JobExecutionContext context) throws JobExecutionException {    	
    	IJob job = SchedulerManager.getInstance(this.getClassName());
        if (job == null) {
            return;
        }
        JobDetail jobDetail = context.getJobDetail();
        JobKey jobKey = jobDetail.getKey();
        String tenantUuid = jobKey.getGroup();
		TenantContext tenantContext = TenantContext.init(tenantUuid);
		tenantContext.setUseDefaultDatasource(false);
        Long jobId = Long.parseLong(context.getJobDetail().getKey().getName());
        JobVo jobVo = schedulerMapper.getJobById(jobId);         
        if (jobVo.getServerId() == Config.SCHEDULE_SERVER_ID) {
            if (JobVo.YES.equals(jobVo.getNeedAudit())) {
                JobAuditVo auditVo = new JobAuditVo(jobId);
                schedulerMapper.insertJobAudit(auditVo);
                String path = getFilePath();
                File logFile = new File(path + auditVo.getId() + ".log");
                auditVo.setLogPath(logFile.getPath());
                OutputStreamWriter logOut = null;

                try {
                    if (!logFile.getParentFile().exists()) {
                        logFile.getParentFile().mkdirs();
                    }

                    if (!logFile.exists()) {
                        logFile.createNewFile();
                    }
                    logOut = new OutputStreamWriter(new FileOutputStream(logFile, true), "UTF-8");

                    if (logOut != null) {
                        context.put("logOutput", logOut);
                    }

                    job.executeInternal(context);                   
                    auditVo.setState(JobAuditVo.SUCCESS);                    
                } catch (Exception ex) {
                    try {
                        auditVo.setState(JobAuditVo.ERROR);
                        logger.error(ex.getMessage(), ex);
                        logOut.write(ExceptionUtils.getStackTrace(ex));
                    } catch (IOException e) {
                        logger.error(e.getMessage(), e);
                    }
                } finally {
                    schedulerMapper.updateJobAudit(auditVo);
                    if (logOut != null) {
                        try {
                            logOut.flush();
                            logOut.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }else {
            	job.executeInternal(context);
            }
            JobVo schedule = new JobVo();
    		schedule.setLastFinishTime(new Date());
    		schedule.setLastFireTime(context.getFireTime());
    		schedule.setNextFireTime(context.getNextFireTime());
    		schedule.setId(jobId);
    		schedule.setExecCount(jobVo.getExecCount()+1);
    		schedulerMapper.updateJobById(schedule);
        } else {
            JobObject jobObject = JobObject.buildJobObject(jobVo);
            scheduleManager.deleteJob(jobObject);
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

    private String getFilePath() {
        Calendar calendar = Calendar.getInstance();
        String path = Config.SCHEDULE_AUDIT_PATH + File.separator + calendar.get(Calendar.YEAR) + File.separator + (calendar.get(Calendar.MONTH) +1)+ File.separator + calendar.get(Calendar.DAY_OF_MONTH) + File.separator;
        path = path.replace(Config.TENANT_UUID,TenantContext.get().getTenantUuid());
        return path;
    }

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
