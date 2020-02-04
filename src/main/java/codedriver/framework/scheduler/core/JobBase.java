package codedriver.framework.scheduler.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
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
import codedriver.framework.scheduler.dto.JobAuditVo;
import codedriver.framework.scheduler.dto.JobLockVo;
import codedriver.framework.scheduler.dto.JobObject;
import codedriver.framework.scheduler.dto.JobPropVo;
import codedriver.framework.scheduler.dto.JobStatusVo;
import codedriver.framework.scheduler.dto.JobVo;
import codedriver.framework.scheduler.exception.ScheduleHandlerNotFoundException;
import codedriver.framework.scheduler.exception.ScheduleIllegalParameterException;
import codedriver.framework.scheduler.exception.ScheduleParamNotExistsException;
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
		Date fireTime = new Date();
		JobDetail jobDetail = context.getJobDetail();
		JobObject jobObject = (JobObject) jobDetail.getJobDataMap().get("jobObject");
		JobKey jobKey = jobDetail.getKey();
		String jobName = jobKey.getName();
		String jobGroup = jobKey.getGroup();
		String tenantUuid = jobObject.getTenantUuid();
		// 从job组名中获取租户uuid,切换到租户的数据源
		TenantContext.init(tenantUuid).setUseDefaultDatasource(false);
		// 检查作业是否需要重新加载
		IJob jobHandler = SchedulerManager.getHandler(this.getClassName());
		if (jobHandler == null) {
			schedulerManager.unloadJob(jobObject);
			throw new ScheduleHandlerNotFoundException(jobObject.getJobClassName());
		}
		if (!jobHandler.checkCronIsExpired(jobObject)) {
			jobHandler.reloadJob(jobObject);
			return;
		}
		JobStatusVo beforeJobStatusVo = schedulerMapper.getJobStatusByJobNameGroup(jobName, jobGroup);
		JobLockVo jobLockVo = schedulerService.getJobLock(jobName, jobGroup);
		// 取不到锁，不允许执行
		if (jobLockVo == null) {
			return;
		}
		JobStatusVo oldJobStatusVo = schedulerMapper.getJobStatusByJobNameGroup(jobName, jobGroup);
		// 前后执行次数不一致，证明已经执行过，直接退出
		if (beforeJobStatusVo.getExecCount().intValue() != oldJobStatusVo.getExecCount().intValue()) {
			return;
		}
		try {

			JobVo jobVo = schedulerMapper.getJobBaseInfoByUuid(jobName);
			// 如果作业存在并且设置为需要审计
			if (jobVo != null && jobVo.getNeedAudit().equals(1)) {
				JobAuditVo auditVo = new JobAuditVo(jobName, Config.SCHEDULE_SERVER_ID);
				schedulerMapper.insertJobAudit(auditVo);
				jobDetail.getJobDataMap().put("jobAuditVo", auditVo);
				try {
					jobHandler.executeInternal(context);
					auditVo.setStatus(JobAuditVo.SUCCEED);
				} catch (Exception ex) {
					auditVo.setStatus(JobAuditVo.FAILED);
					auditVo.appendContent(ExceptionUtils.getStackTrace(ex));
					logger.error(ex.getMessage(), ex);
				} finally {
					schedulerMapper.updateJobAudit(auditVo);
				}
			} else {
				jobHandler.executeInternal(context);
			}
			// 执行完业务逻辑后，更新定时作业状态信息
			JobStatusVo jobStatus = new JobStatusVo();
			jobStatus.setLastFinishTime(new Date());
			jobStatus.setLastFireTime(fireTime);

			if (context.getNextFireTime() != null) {
				jobStatus.setNextFireTime(context.getNextFireTime());
			} else {
				// 没有下次执行时间，则unload作业，清除作业相关信息。
				schedulerManager.unloadJob(jobObject);
				schedulerMapper.deleteJobStatus(jobObject.getJobName(), jobObject.getJobGroup());
				schedulerMapper.deleteJobLock(jobObject.getJobName(), jobObject.getJobGroup());
			}
			jobStatus.setJobName(jobName);
			jobStatus.setJobGroup(jobGroup);
			jobStatus.setExecCount(oldJobStatusVo.getExecCount() + 1);
			schedulerMapper.updateJobStatus(jobStatus);
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
		} finally {
			// 恢复作业锁状态为等待中

			jobLockVo.setServerId(Config.SCHEDULE_SERVER_ID);
			jobLockVo.setLock(JobLockVo.WAITING);
			schedulerService.updateJobLock(jobLockVo);
		}
	}

	/**
	 * 主要定时方法实现区
	 */
	@Override
	public abstract void executeInternal(JobExecutionContext context) throws JobExecutionException;

	// private String getFilePath() {
	// Calendar calendar = Calendar.getInstance();
	// String path = Config.SCHEDULE_AUDIT_PATH + File.separator +
	// calendar.get(Calendar.YEAR) + File.separator +
	// (calendar.get(Calendar.MONTH) +1)+ File.separator +
	// calendar.get(Calendar.DAY_OF_MONTH) + File.separator;
	// path =
	// path.replace(Config.TENANT_UUID,TenantContext.get().getTenantUuid());
	// return path;
	// }

	@Override
	public boolean valid(List<JobPropVo> propVoList) {
		Map<String, Param> paramMap = initProp();
		if (paramMap.isEmpty()) {
			return true;
		}
		if (propVoList != null && propVoList.size() > 0) {
			for (JobPropVo jobProp : propVoList) {
				if (jobProp.getValue() == null || "".equals(jobProp.getValue())) {
					continue;
				}
				Param param = paramMap.get(jobProp.getName());
				if (param == null) {
					continue;
				}
				String dataType = param.dataType();
				if ("int".equals(dataType) || "Integer".equals(dataType)) {
					try {
						Integer.parseInt(jobProp.getValue());
					} catch (NumberFormatException e) {
						logger.error(e.getMessage(), e);
						throw new ScheduleIllegalParameterException("定时作业参数类型不匹配，参数" + jobProp.getName() + "的类型是" + dataType);
					}
				} else if ("long".equals(dataType) || "Long".equals(dataType)) {
					try {
						Long.parseLong(jobProp.getValue());
					} catch (NumberFormatException e) {
						logger.error(e.getMessage(), e);
						throw new ScheduleIllegalParameterException("定时作业参数类型不匹配，参数" + jobProp.getName() + "的类型是" + dataType);
					}
				} else if ("double".equals(dataType) || "Double".equals(dataType)) {
					try {
						Double.parseDouble(dataType);
					} catch (NumberFormatException e) {
						logger.error(e.getMessage(), e);
						throw new ScheduleIllegalParameterException("定时作业参数类型不匹配，参数" + jobProp.getName() + "的类型是" + dataType);
					}
				}
				paramMap.remove(jobProp.getName(), param);
			}
		}
		// 检查是否有必传参数没传
		for (Entry<String, Param> entry : paramMap.entrySet()) {
			Param param = entry.getValue();
			if (param.required() == true) {
				throw new ScheduleParamNotExistsException(this.getClassName(), param.name());
			}
		}
		return true;
	}

	@Override
	public Map<String, Param> initProp() {
		Map<String, Param> paramMap = new HashMap<>();
		try {
			Method method = this.getClass().getDeclaredMethod("executeInternal", JobExecutionContext.class);
			if (method == null || !method.isAnnotationPresent(Input.class)) {
				return paramMap;
			}
			for (Annotation anno : method.getDeclaredAnnotations()) {
				if (anno.annotationType().equals(Input.class)) {
					Input input = (Input) anno;
					Param[] params = input.value();
					for (Param param : params) {
						paramMap.put(param.name(), param);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return paramMap;
	}
}
