/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.scheduler.core;

import codedriver.framework.asynchronization.threadlocal.InputFromContext;
import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.asynchronization.threadlocal.UserContext;
import codedriver.framework.common.config.Config;
import codedriver.framework.common.constvalue.InputFrom;
import codedriver.framework.common.constvalue.SystemUser;
import codedriver.framework.exception.core.ApiRuntimeException;
import codedriver.framework.scheduler.annotation.Input;
import codedriver.framework.scheduler.annotation.Param;
import codedriver.framework.scheduler.dao.mapper.SchedulerMapper;
import codedriver.framework.scheduler.dto.*;
import codedriver.framework.scheduler.exception.ScheduleHandlerNotFoundException;
import codedriver.framework.scheduler.exception.ScheduleIllegalParameterException;
import codedriver.framework.scheduler.exception.ScheduleParamNotExistsException;
import codedriver.framework.transaction.util.TransactionUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.TransactionStatus;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 1、定时任务处理模块基类，所新增的定时任务类必须继承此类。
 * 即内部作业直接继承此类，外部作业继承"PublicJobBase"类。
 * 外部作业使用声明请移步"PublicJobBase"类。
 * 2、内部作业默认审计
 * 3、内部作业的相关配置（job_name,job_group,cron、是否审计、计划开始时间等）在内部作业loadJob自己定义
 * 4、`schedule_job_audit` 和  `schedule_job_audit_detail` 用于所有作业审计记录
 * 5、`schedule_job_lock` 用于所有作业在服务器之间做负载均衡
 * 6、`schedule_job_status` 用于记录所有作业的执行状态（上一次|下次激活时间、执行次数等），执行job前后会更新
 */

public abstract class JobBase implements IJob {

    private final Logger logger = LoggerFactory.getLogger(JobBase.class);
    protected static SchedulerMapper schedulerMapper;

    protected static SchedulerManager schedulerManager;

    private static TransactionUtil transactionUtil;

    @Autowired
    public void setTransactionUtil(TransactionUtil _transactionUtil) {
        transactionUtil = _transactionUtil;
    }

    @Autowired
    public void setSchedulerMapper(SchedulerMapper schMapper) {
        schedulerMapper = schMapper;
    }

    @Autowired
    protected void setSchedulerManager(SchedulerManager schManager) {
        schedulerManager = schManager;
    }

    private JobLockVo getJobLock(String jobName, String jobGroup) {
        // 开启事务，获取作业锁
        TransactionStatus ts = transactionUtil.openTx();
        JobLockVo jobLockVo = schedulerMapper.getJobLockByJobNameGroup(jobName, jobGroup);

        if (jobLockVo != null) {
            // 如果锁的状态是running状态，证明其他节点已经在执行，直接返回
            if (jobLockVo.getLock().equals(JobLockVo.RUNNING) && !jobLockVo.getServerId().equals(Config.SCHEDULE_SERVER_ID)) {
                jobLockVo = null;
            }
        }
        if (jobLockVo != null) {
            // 修改锁状态
            jobLockVo.setServerId(Config.SCHEDULE_SERVER_ID);
            jobLockVo.setLock(JobLockVo.RUNNING);
            schedulerMapper.updateJobLock(jobLockVo);
        }
        transactionUtil.commitTx(ts);
        return jobLockVo;
    }

    private void updateJobLockAndStatus(JobLockVo jobLockVo, JobStatusVo jobStatusVo) {
        // 开启事务，获取作业锁
        TransactionStatus ts = transactionUtil.openTx();
        schedulerMapper.updateJobStatus(jobStatusVo);
        schedulerMapper.updateJobLock(jobLockVo);
        transactionUtil.commitTx(ts);
    }

    @Override
    public final void execute(JobExecutionContext context) throws JobExecutionException {
        InputFromContext.init(InputFrom.CRON);
        Date fireTime = new Date();
        JobDetail jobDetail = context.getJobDetail();
        JobObject jobObject = (JobObject) jobDetail.getJobDataMap().get("jobObject");
        JobKey jobKey = jobDetail.getKey();
        String jobName = jobKey.getName();
        String jobGroup = jobKey.getGroup();
        String tenantUuid = jobObject.getTenantUuid();
        // 从job组名中获取租户uuid,切换到租户的数据源
        TenantContext.init(tenantUuid).setUseDefaultDatasource(false);
        UserContext.init(SystemUser.SYSTEM.getUserVo(), SystemUser.SYSTEM.getTimezone());
        // 检查作业是否需要重新加载
        IJob jobHandler = SchedulerManager.getHandler(this.getClassName());
        if (jobHandler == null) {
            schedulerManager.unloadJob(jobObject);
            throw new ScheduleHandlerNotFoundException(jobObject.getJobHandler());
        }
        if (!jobHandler.isHealthy(jobObject)) {
            schedulerManager.unloadJob(jobObject);
            return;
        }
        Date currentFireTime = context.getFireTime();// 本次执行激活时间
        JobStatusVo beforeJobStatusVo = schedulerMapper.getJobStatusByJobNameGroup(jobName, jobGroup);
        // 如果数据库中记录的下次激活时间在本次执行激活时间之后，则放弃执行业务逻辑
        if (beforeJobStatusVo == null || (beforeJobStatusVo.getNextFireTime() != null && beforeJobStatusVo.getNextFireTime().after(currentFireTime))) {
            return;
        }

        JobLockVo jobLockVo = getJobLock(jobName, jobGroup);

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
            Boolean isAudit = false;
            //判断外部作业是否需要审计
            if (jobHandler instanceof IPublicJob) {//兼容事务动态代理，改为通过接口判断
                JobVo jobVo = schedulerMapper.getJobBaseInfoByUuid(jobName);
                if (jobVo != null && jobVo.getNeedAudit().equals(1)) {
                    isAudit = true;
                }
            } else {
                isAudit = jobHandler.isAudit();
            }
            // 如果作业存在并且设置为需要审计
            if (isAudit) {
                JobAuditVo auditVo = new JobAuditVo(jobName, Config.SCHEDULE_SERVER_ID);
                schedulerMapper.insertJobAudit(auditVo);
                jobDetail.getJobDataMap().put("jobAuditVo", auditVo);
                try {
                    jobHandler.executeInternal(context, jobObject);
                    auditVo.setStatus(JobAuditVo.Status.SUCCEED.getValue());
                } catch (ApiRuntimeException ex) {
                    auditVo.setStatus(JobAuditVo.Status.FAILED.getValue());
                    auditVo.appendContent(ex.getMessage(true));
                } catch (Exception ex) {
                    auditVo.setStatus(JobAuditVo.Status.FAILED.getValue());
                    auditVo.appendContent(ExceptionUtils.getStackTrace(ex));
                    logger.error(ex.getMessage(), ex);
                } finally {
                    if (StringUtils.isNotBlank(auditVo.getContentHash()) && schedulerMapper.checkJobAuditDetailIsExists(auditVo.getContentHash()) == 0) {
                        schedulerMapper.insertJobAuditDetail(auditVo.getContentHash(), auditVo.getContent());
                    }
                    schedulerMapper.updateJobAudit(auditVo);
                }
            } else {
                jobHandler.executeInternal(context, jobObject);
            }
/*
  异步模式，如果事务hold住时间太长，可以考虑使用异步模式，但作业的执行时间需要手动处理

            // 如果作业存在并且设置为需要审计
            if (isAudit) {
                JobAuditVo auditVo = new JobAuditVo(jobName, Config.SCHEDULE_SERVER_ID);
                schedulerMapper.insertJobAudit(auditVo);
                jobDetail.getJobDataMap().put("jobAuditVo", auditVo);
                CachedThreadPool.execute(new CodeDriverThread(jobObject.getJobName()) {
                    @Override
                    protected void execute() {
                        try {
                            jobHandler.executeInternal(context, jobObject);
                            auditVo.setStatus(JobAuditVo.Status.SUCCEED.getValue());
                        } catch (ApiRuntimeException ex) {
                            auditVo.setStatus(JobAuditVo.Status.FAILED.getValue());
                            auditVo.appendContent(ex.getMessage(true));
                        } catch (Exception e) {
                            auditVo.setStatus(JobAuditVo.Status.FAILED.getValue());
                            auditVo.appendContent(ExceptionUtils.getStackTrace(e));
                            logger.error(e.getMessage(), e);
                        } finally {
                            if (StringUtils.isNotBlank(auditVo.getContentHash())) {
                                schedulerMapper.insertJobAuditDetail(auditVo.getContentHash(), auditVo.getContent());
                            }
                            schedulerMapper.updateJobAudit(auditVo);
                        }
                    }
                });
            } else {
                CachedThreadPool.execute(new CodeDriverThread(jobObject.getJobName()) {
                    @Override
                    protected void execute() {
                        try {
                            jobHandler.executeInternal(context, jobObject);
                        } catch (Exception ignored) {
                        }
                    }
                });
            }
 */


            // 执行完业务逻辑后，更新定时作业状态信息

            oldJobStatusVo.setLastFinishTime(new Date());
            oldJobStatusVo.setLastFireTime(fireTime);

            if (context.getNextFireTime() != null) {
                oldJobStatusVo.setNextFireTime(context.getNextFireTime());
            } else {
                // 没有下次执行时间，则unload作业，清除作业相关信息。
                schedulerManager.unloadJob(jobObject);
                schedulerMapper.deleteJobStatus(jobObject.getJobName(), jobObject.getJobGroup());
                schedulerMapper.deleteJobLock(jobObject.getJobName(), jobObject.getJobGroup());
            }

            oldJobStatusVo.setExecCount(oldJobStatusVo.getExecCount() + 1);
        } catch (ApiRuntimeException ex) {
            //能识别的exception,不打error日志
            logger.debug(ex.getMessage(), ex);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        } finally {
            // 恢复作业锁状态为等待中
            jobLockVo.setServerId(Config.SCHEDULE_SERVER_ID);
            jobLockVo.setLock(JobLockVo.WAITING);
            updateJobLockAndStatus(jobLockVo, oldJobStatusVo);
        }
    }

    /**
     * 主要定时方法实现区
     */
    @Override
    public abstract void executeInternal(JobExecutionContext context, JobObject jobObject) throws Exception;

    @Override
    public Boolean valid(List<JobPropVo> propVoList) {
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
            if (param.required()) {
                throw new ScheduleParamNotExistsException(this.getClassName(), param.name());
            }
        }
        return true;
    }

    @Override
    public Map<String, Param> initProp() {
        Map<String, Param> paramMap = new HashMap<>();
        try {
            Method method = this.getClass().getDeclaredMethod("executeInternal", JobExecutionContext.class, JobObject.class);
            if (!method.isAnnotationPresent(Input.class)) {
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
            logger.error(e.getMessage(), e);
        }
        return paramMap;
    }
}
