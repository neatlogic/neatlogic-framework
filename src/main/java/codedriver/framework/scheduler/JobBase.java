package codedriver.framework.scheduler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import codedriver.framework.common.config.Config;
import codedriver.framework.dao.mapper.ScheduleJobAuditMapper;
import codedriver.framework.dao.mapper.ScheduleMapper;
import codedriver.framework.scheduler.annotation.Input;
import codedriver.framework.scheduler.annotation.Param;
import codedriver.framework.scheduler.dto.JobObject;
import codedriver.framework.scheduler.dto.JobPropVo;
import codedriver.framework.scheduler.dto.JobVo;
import codedriver.framework.scheduler.dto.ScheduleJobAuditVo;

/**
 * 定时任务处理模块基类，所新增的定时任务类必须继承此类，新Job类必须实现接口内的2个方法。
 */
public abstract class JobBase implements IJob {

	Logger logger = LoggerFactory.getLogger(JobBase.class);
	protected static ScheduleMapper scheduleMapper;

	protected static SchedulerManager scheduleManager;

	protected static ScheduleJobAuditMapper scheduleJobAuditMapper;

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	public void setScheduleMapper(ScheduleMapper schMapper) {
		scheduleMapper = schMapper;
	}

	@Autowired
	private void setScheduleManager(SchedulerManager schManager) {
		scheduleManager = schManager;
	}

	@Autowired
	public void setScheduleJobAuditMapper(ScheduleJobAuditMapper schJobAuditMapper) {
		scheduleJobAuditMapper = schJobAuditMapper;
	}

	private static Map<String, IJob> jobBaseMap = new HashMap<String, IJob>();

	@PostConstruct
	public final void init() {
		jobBaseMap.put(this.getClass().getName(), (IJob) applicationContext.getBean("job-" + this.getJobClassId()));
	}

    @Override
    public final void execute(JobExecutionContext context) throws JobExecutionException {
    	String jobClassName = this.getClass().getName();
        if (!jobBaseMap.containsKey(jobClassName)) {
            return;
        }
        Long jobId = Long.parseLong(context.getJobDetail().getKey().getName());
        JobVo jobVo = scheduleMapper.getJobById(jobId);         
        if (jobVo.getServerId() == Config.SCHEDULE_SERVER_ID) {
            if (jobVo.getNeedAudit() == 1) {
                ScheduleJobAuditVo auditVo = new ScheduleJobAuditVo(jobId);
                scheduleJobAuditMapper.insertScheduleJobAudit(auditVo);
                String path = getFilePath();
                File logFile = new File(path + auditVo.getId() + ".log");
                File errFile = new File(path + auditVo.getId() + ".err");

                auditVo.setLogPath(logFile.getPath());
                auditVo.setErrPath(errFile.getPath());

                OutputStreamWriter logOut = null;
                OutputStreamWriter errOut = null;

                try {
                    if (!logFile.getParentFile().exists()) {
                        logFile.getParentFile().mkdirs();
                    }

                    if (!logFile.exists()) {
                        logFile.createNewFile();
                    }
                    logOut = new OutputStreamWriter(new FileOutputStream(logFile, true), "UTF-8");

                    if (!errFile.getParentFile().exists()) {
                        errFile.getParentFile().mkdirs();
                    }
                    if (!errFile.exists()) {
                        errFile.createNewFile();
                    }
                    errOut = new OutputStreamWriter(new FileOutputStream(errFile, true), "UTF-8");

                    if (logOut != null) {
                        context.put("logOutput", logOut);
                    }

                    if (errOut != null) {
                        context.put("errOutput", errOut);
                    }
                    jobBaseMap.get(jobClassName).executeInternal(context);
                    auditVo.setState(ScheduleJobAuditVo.JobAuditState.FINISH.getName());
                } catch (Exception ex) {
                    try {
                        auditVo.setState(ScheduleJobAuditVo.JobAuditState.FAULT.getName());
                        logger.error(ex.getMessage(), ex);
                        errOut.write(ExceptionUtils.getStackTrace(ex));
                    } catch (IOException e) {
                        logger.error(e.getMessage(), e);
                    }
                } finally {
                    scheduleJobAuditMapper.updateScheduleJobAudit(auditVo);
                    if (logOut != null) {
                        try {
                            logOut.flush();
                            logOut.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (errOut != null) {
                        try {
                            errOut.flush();
                            errOut.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }else {
                jobBaseMap.get(jobClassName).executeInternal(context);
            }
        } else {
            JobObject jobObject = jobVo.buildJobObject();
            scheduleManager.deleteJob(jobObject);
        }
    }

	/**
	 * 主要定时方法实现区
	 */
	@Override
	public abstract void executeInternal(JobExecutionContext context) throws JobExecutionException;

	/**
	 * 获取类定义的id，对应的定义表：schedule_jobclass。
	 *
	 */
	@Override
	public abstract Integer getJobClassId();

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
        return path;
    }

    @Override
    public boolean valid(List<JobPropVo> propVoList) {
	    if (propVoList == null || propVoList.size() == 0){
	    	return true;
	    }
	    Map<String, Param> paramMap = initProp();
        if (paramMap.isEmpty()){
        	return true;
        }
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
//            boolean isInt = dataType.equals("int")||dataType.equals("Integer");
//            boolean isLong = dataType.equals("long")||dataType.equals("Long");
//            if (isInt||isLong){
//                try{
//                	String testStr = new BigDecimal(jobProp.getValue()).toString();
//                }catch (Exception e){
//                    return false;
//                }
//            }
            paramMap.remove(jobProp.getName(), param);
        }
        // 检查是否有必传参数没传
        for(Entry<String, Param> entry : paramMap.entrySet()) {
        	Param param = entry.getValue();
        	if(param.required() == true) {
        		throw new RuntimeException("jobClass为" + this.getClass().getName()+","+param.name()+"是必填参数");
        	}
        }
        return true;
    }

    @Override
    public final Map<String, Param> initProp() {
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
