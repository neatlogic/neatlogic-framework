/*Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.*/

package neatlogic.framework.scheduler.core;

import neatlogic.framework.asynchronization.threadlocal.InputFromContext;
import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.asynchronization.threadlocal.UserContext;
import neatlogic.framework.common.config.Config;
import neatlogic.framework.common.constvalue.InputFrom;
import neatlogic.framework.common.constvalue.SystemUser;
import neatlogic.framework.common.util.TenantUtil;
import neatlogic.framework.exception.core.ApiRuntimeException;
import neatlogic.framework.scheduler.annotation.Param;
import neatlogic.framework.scheduler.annotation.Prop;
import neatlogic.framework.scheduler.dao.mapper.SchedulerMapper;
import neatlogic.framework.scheduler.dto.*;
import neatlogic.framework.scheduler.exception.ScheduleHandlerNotFoundException;
import neatlogic.framework.scheduler.exception.ScheduleIllegalParameterException;
import neatlogic.framework.scheduler.exception.ScheduleParamNotExistsException;
import neatlogic.framework.transaction.util.TransactionUtil;
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
        JobLockVo jobLockVo;
        try {
            jobLockVo = schedulerMapper.getJobLockByJobNameGroup(jobName, jobGroup);
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
        }finally {
            transactionUtil.commitTx(ts);
        }
        return jobLockVo;
    }

    private void updateJobLockAndStatus(JobLockVo jobLockVo, JobStatusVo jobStatusVo) {
        // 开启事务，获取作业锁
        TransactionStatus ts = transactionUtil.openTx();
        try {
            schedulerMapper.updateJobStatus(jobStatusVo);
            schedulerMapper.updateJobLock(jobLockVo);
        }finally {
            transactionUtil.commitTx(ts);
        }
    }


    @Override
    public final Boolean isHealthy(JobObject jobObject) {
//        JobLoadTimeVo jobLoadTime = schedulerMapper.getJobLoadTime(new JobLoadTimeVo(jobObject.getJobName(), jobObject.getJobGroup()));
//        if (jobLoadTime == null) {
//            return false;
//        }
//        if (!Objects.equals(jobLoadTime.getCron(), jobObject.getCron())) {
//            return false;
//        }
//        if (!Objects.equals(jobLoadTime.getLoadTime(), jobObject.getLoadTime())) {
//            return false;
//        }
        return isMyHealthy(jobObject);
    }

    protected abstract Boolean isMyHealthy(JobObject jobObject);

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
        //如果租户不存在则不执行该租户的作业
        if(!TenantUtil.hasTenant(tenantUuid)){
            return;
        }
        // 从job组名中获取租户uuid,切换到租户的数据源
        TenantContext.init(tenantUuid).setUseDefaultDatasource(false);
        UserContext.init(SystemUser.SYSTEM);
        // 检查作业是否需要重新加载
        IJob jobHandler = SchedulerManager.getHandler(this.getClassName());
        if (jobHandler == null) {
            schedulerManager.unloadJob(jobObject);
            throw new ScheduleHandlerNotFoundException(jobObject.getJobHandler());
        }
        if (!jobHandler.isHealthy(jobObject)) {
            // not healthy 不能unloadJob 否则会删除作业状态和锁，导致正常接管的server也无法跑作业。例如：A Server 修改cron 每天0点跑作业。 然后B Server 修改cron每分钟跑。 当A Server 发现not healthy 则会unload 并删除status，lock。后续判断会导致B Server 也不会再跑作业。
            //schedulerManager.unloadJob(jobObject);
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
                auditVo.setStatus(JobAuditVo.Status.RUNNING.getValue());
                auditVo.setCron(jobObject.getCron());
                auditVo.setNextFireTime(context.getNextFireTime());
                schedulerMapper.insertJobAudit(auditVo);
                jobDetail.getJobDataMap().put("jobAuditVo", auditVo);
                try {
                    jobHandler.executeInternal(context, jobObject);
                    auditVo.setStatus(JobAuditVo.Status.SUCCEED.getValue());
                } catch (ApiRuntimeException ex) {
                    auditVo.setStatus(JobAuditVo.Status.FAILED.getValue());
                    auditVo.appendContent(ex.getMessage());
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
                CachedThreadPool.execute(new NeatLogicThread(jobObject.getJobName()) {
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
                CachedThreadPool.execute(new NeatLogicThread(jobObject.getJobName()) {
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
            if (!method.isAnnotationPresent(Prop.class)) {
                return paramMap;
            }
            for (Annotation anno : method.getDeclaredAnnotations()) {
                if (anno.annotationType().equals(Prop.class)) {
                    Prop input = (Prop) anno;
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
