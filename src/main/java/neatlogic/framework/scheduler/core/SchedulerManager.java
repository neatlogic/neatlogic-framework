/*
 *
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the NeatLogic Sustainable Use License (NSUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */

package neatlogic.framework.scheduler.core;

import neatlogic.framework.applicationlistener.core.ModuleInitializedListenerBase;
import neatlogic.framework.asynchronization.thread.NeatLogicThread;
import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.asynchronization.threadlocal.UserContext;
import neatlogic.framework.asynchronization.threadpool.CachedThreadPool;
import neatlogic.framework.bootstrap.NeatLogicWebApplicationContext;
import neatlogic.framework.common.RootComponent;
import neatlogic.framework.common.config.Config;
import neatlogic.framework.common.constvalue.systemuser.SystemUser;
import neatlogic.framework.dao.mapper.TenantMapper;
import neatlogic.framework.dto.TenantVo;
import neatlogic.framework.dto.module.ModuleGroupVo;
import neatlogic.framework.heartbeat.core.HeartbeatManager;
import neatlogic.framework.scheduler.dao.mapper.SchedulerMapper;
import neatlogic.framework.scheduler.dto.*;
import neatlogic.framework.scheduler.enums.JobLoadTriggerType;
import neatlogic.framework.util.$;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

@RootComponent
public class SchedulerManager extends ModuleInitializedListenerBase {
    private static final Logger logger = LoggerFactory.getLogger(SchedulerManager.class);

    private static final Map<String, IJob> jobHandlerMap = new HashMap<>();
    private static final Map<String, JobClassVo> jobClassMap = new HashMap<>();
    private static final List<JobClassVo> jobClassList = new ArrayList<>();
    private static final List<JobClassVo> publicJobClassList = new ArrayList<>();
    private static final ReentrantLock GLOBAL_LOCK = new ReentrantLock();
    private static SchedulerFactoryBean staticSchedulerFactoryBean;

    @Resource
    private TenantMapper tenantMapper;
    @Resource
    private SchedulerFactoryBean schedulerFactoryBean;
    @Resource
    private SchedulerMapper schedulerMapper;

    private List<TenantVo> tenantList = new ArrayList<>();

    protected void myInit() {
        tenantList = tenantMapper.getAllActiveTenant();
        staticSchedulerFactoryBean = schedulerFactoryBean;
    }

    public static IJob getHandler(String className) {
        return jobHandlerMap.get(className);
    }

    public static List<JobClassVo> getAllPublicJobClassList() {
        return publicJobClassList;
    }

    public static List<JobClassVo> getAllJobClassList() {
        return jobClassList;
    }

    public static JobClassVo getJobClassByClassName(String className) {
        JobClassVo jobClassVo = jobClassMap.get(className);
        if (jobClassVo != null && TenantContext.get().containsModule(jobClassVo.getModuleId())) {
            return jobClassVo;
        }
        return null;
    }

    public boolean checkJobIsExists(String jobName, String jobGroup) {
        JobKey jobKey = new JobKey(jobName, jobGroup);
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        try {
            if (scheduler.getJobDetail(jobKey) != null) {
                return true;
            }
        } catch (SchedulerException e) {
            logger.error(e.getMessage(), e);
        }
        return false;
    }

    public static boolean checkJobIsLoad(String jobName, String jobGroup) {
        JobKey jobKey = new JobKey(jobName, jobGroup);
        Scheduler scheduler = staticSchedulerFactoryBean.getScheduler();
        try {
            if (scheduler.getJobDetail(jobKey) != null) {
                return true;
            }
        } catch (SchedulerException e) {
            logger.error(e.getMessage(), e);
        }
        return false;
    }

    public void saveJobSource(JobObject jobObject) {
        ScheduleJobSourceVo scheduleJobSource = schedulerMapper.getJobSourceByJobNameAndJobGroup(jobObject.getJobName(), jobObject.getJobGroup());
        if (scheduleJobSource == null) {
            ScheduleJobSourceVo scheduleJobSourceVo = new ScheduleJobSourceVo();
            scheduleJobSourceVo.setJobName(jobObject.getJobName());
            scheduleJobSourceVo.setJobGroup(jobObject.getJobGroup());
            scheduleJobSourceVo.setServerId(Config.SCHEDULE_SERVER_ID);
            scheduleJobSourceVo.setServerGroup(Config.SCHEDULE_SERVER_GROUP());
            scheduleJobSourceVo.setFcu(UserContext.get().getUserUuid());
            scheduleJobSourceVo.setLcu(UserContext.get().getUserUuid());
            schedulerMapper.insertJobSource(scheduleJobSourceVo);
        }
    }

    public void deleteJobSource(JobObject jobObject) {
        schedulerMapper.deleteJobSourceByJobNameAndJobGroup(jobObject.getJobName(), jobObject.getJobGroup());
    }

    public boolean checkJobSourceServerGroup(String jobName, String jobGroup) {
        // 重启服务器时，只加载相同分组或不属于任何分组(历史旧数据)的作业
        ScheduleJobSourceVo scheduleJobSource = schedulerMapper.getJobSourceByJobNameAndJobGroup(jobName, jobGroup);
        if (scheduleJobSource != null) {
            if (!Objects.equals(scheduleJobSource.getServerGroup(), Config.SCHEDULE_SERVER_GROUP())) {
                return false;
            }
        }
        return true;
    }

    /**
     * 加载定时作业，同时设置定时作业状态和锁
     *
     * @param jobObject 作业信息
     * @return 日期
     */
    public Date loadJob(JobObject jobObject, JobLoadTriggerType triggerType) {
        if (triggerType == JobLoadTriggerType.INITIAL_CREATE) {
            saveJobSource(jobObject);
        }
        // 重启服务器时，只加载相同分组或不属于任何分组(历史旧数据)的作业
        if (!checkJobSourceServerGroup(jobObject.getJobName(), jobObject.getJobGroup())) {
            return null;
        }
        // 如果结束时间比当前时间早，就不加载了
        if (jobObject.getEndTime() != null && jobObject.getEndTime().before(new Date())) {
            return null;
        }
        try {
            String jobName = jobObject.getJobName();
            String jobGroup = jobObject.getJobGroup();
            String className = jobObject.getJobHandler();

            JobKey jobKey = new JobKey(jobName, jobGroup);
            Scheduler scheduler = schedulerFactoryBean.getScheduler();

            try {
                JobLockVo jobLockVo = schedulerMapper.getJobLockByJobNameGroup(jobName, jobGroup);
                if (jobLockVo == null) {
                    jobLockVo = new JobLockVo(jobName, jobGroup, className);
                    schedulerMapper.insertJobLock(jobLockVo);
                }

                TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger().withIdentity(jobName, jobGroup);
                if (StringUtils.isNotBlank(jobObject.getCron()) && CronExpression.isValidExpression(jobObject.getCron())) {
                    CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(jobObject.getCron());
                    if (Boolean.TRUE.equals(jobObject.getCronMisfireDoNothing())) {
                        cronScheduleBuilder = cronScheduleBuilder.withMisfireHandlingInstructionDoNothing();
                    }
                    triggerBuilder.withSchedule(cronScheduleBuilder);
                } else if (jobObject.getIntervalInSeconds() != null && jobObject.getIntervalInSeconds() > 0) {
                    SimpleScheduleBuilder ssb = SimpleScheduleBuilder.simpleSchedule();
                    ssb = ssb.withIntervalInSeconds(jobObject.getIntervalInSeconds());
                    if (jobObject.getRepeatCount() != null) {
                        ssb.withRepeatCount(jobObject.getRepeatCount());
                    } else {
                        ssb = ssb.repeatForever();
                    }
                    triggerBuilder.withSchedule(ssb);
                } /*else {

                    return null;
                }*/
                Date startTime = jobObject.getBeginTime();
                if (startTime != null && startTime.after(new Date())) {
                    triggerBuilder.startAt(startTime);
                } else {
                    triggerBuilder.startNow();
                }
                triggerBuilder.endAt(jobObject.getEndTime());
                Trigger trigger = triggerBuilder.build();
                Class clazz = Class.forName(jobObject.getJobHandler());
                JobDetail jobDetail = JobBuilder.newJob(clazz).withIdentity(jobKey).build();
                jobDetail.getJobDataMap().put("jobObject", jobObject);
                // 写入jobstatus (如果数据库不存在job，则需先insert job到数据库，再创建job,否则jobBase 先触发execute，会导致跳过第一次执行)
                JobStatusVo jobStatusVo = schedulerMapper.getJobStatusByJobNameGroup(jobName, jobGroup, System.currentTimeMillis());
                if (jobStatusVo == null) {
                    jobStatusVo = new JobStatusVo();
                    jobStatusVo.setJobName(jobName);
                    jobStatusVo.setJobGroup(jobGroup);
                    jobStatusVo.setHandler(className);
                    schedulerMapper.insertJobStatus(jobStatusVo);
                }
                Date nextFireDate;
                //加上全局锁，避免并发加载同一个作业时出现作业已存在的问题
                GLOBAL_LOCK.lock();
                try {
                    deleteJob(jobName, jobGroup);
                    nextFireDate = scheduler.scheduleJob(jobDetail, trigger);
                    JobLoadVo jobLoadVo = new JobLoadVo();
                    jobLoadVo.setJobName(jobName);
                    jobLoadVo.setJobGroup(jobGroup);
                    jobLoadVo.setServerId(Config.SCHEDULE_SERVER_ID);
                    jobLoadVo.setServerStartTime(HeartbeatManager.START_TIME);
                    schedulerMapper.insertJobLoad(jobLoadVo);
                } finally {
                    GLOBAL_LOCK.unlock();
                }
                if (nextFireDate != null) {
                    jobStatusVo.setNextFireTime(nextFireDate);
                    schedulerMapper.updateJobNextFireTime(jobStatusVo);
                }
//                schedulerMapper.insertJobLoadTime(new JobLoadTimeVo(jobObject.getJobName(), jobObject.getJobGroup(), jobObject.getCron(), jobObject.getLoadTime()));
                return nextFireDate;
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
        return null;
    }

    /**
     * @param jobObject 作业信息
     * @return void
     * @Description: 将定时作业从调度器中删除
     */
    public boolean unloadJob(JobObject jobObject) {
//        schedulerMapper.deleteJobLoadTime(new JobLoadTimeVo(jobObject.getJobName(), jobObject.getJobGroup()));
        deleteJob(jobObject.getJobName(), jobObject.getJobGroup());
        // 清除作业锁和作业状态信息
        schedulerMapper.deleteJobLock(jobObject.getJobName(), jobObject.getJobGroup());
        schedulerMapper.deleteJobStatus(jobObject.getJobName(), jobObject.getJobGroup());
        return true;
    }

    public boolean deleteJob(String jobName, String jobGroup) {
        boolean flag = false;
        try {
            Scheduler scheduler = schedulerFactoryBean.getScheduler();
            JobKey jobKey = new JobKey(jobName, jobGroup);
            if (scheduler.getJobDetail(jobKey) != null) {
                flag = scheduler.deleteJob(jobKey);
                JobLoadVo jobLoadVo = new JobLoadVo();
                jobLoadVo.setJobName(jobName);
                jobLoadVo.setJobGroup(jobGroup);
                jobLoadVo.setServerId(Config.SCHEDULE_SERVER_ID);
                jobLoadVo.setServerStartTime(HeartbeatManager.START_TIME);
                schedulerMapper.deleteJobLoad(jobLoadVo);
            }
        } catch (SchedulerException e) {
            logger.error(e.getMessage(), e);
        }
        return flag;
    }

    /**
     * 卸载当前JVM中指定租户的Quartz作业。
     * 按作业对象中的租户uuid精确匹配，避免使用分组前缀误伤uuid相似的租户。
     *
     * @param tenantUuid 租户uuid
     */
    public void unloadTenantJobs(String tenantUuid) {
        if (StringUtils.isBlank(tenantUuid)) {
            return;
        }
        GLOBAL_LOCK.lock();
        try {
            Scheduler scheduler = schedulerFactoryBean.getScheduler();
            for (String groupName : scheduler.getJobGroupNames()) {
                Set<JobKey> jobKeySet = scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName));
                for (JobKey jobKey : jobKeySet) {
                    JobDetail jobDetail = scheduler.getJobDetail(jobKey);
                    if (jobDetail == null) {
                        continue;
                    }
                    Object jobObject = jobDetail.getJobDataMap().get("jobObject");
                    if (jobObject instanceof JobObject && tenantUuid.equals(((JobObject) jobObject).getTenantUuid())) {
                        scheduler.deleteJob(jobKey);
                    }
                }
            }
        } catch (SchedulerException e) {
            logger.error(e.getMessage(), e);
        } finally {
            GLOBAL_LOCK.unlock();
        }
    }

    /**
     * 重新加载指定租户已启用模块下的调度作业。
     * 显式切回租户库，避免主库数据源模式下生成以null开头的作业分组。
     *
     * @param tenantUuid 租户uuid
     */
    public void loadTenantJobs(String tenantUuid) {
        if (StringUtils.isBlank(tenantUuid)) {
            return;
        }
        TenantContext.get().switchTenant(tenantUuid);
        TenantContext.get().setUseMasterDatabase(false);
        TenantContext.get().switchDefaultDatabase();
        UserContext.init(SystemUser.SYSTEM);
        for (IJob jobHandler : new ArrayList<>(jobHandlerMap.values())) {
            JobClassVo jobClassVo = jobClassMap.get(jobHandler.getClassName());
            if (jobClassVo != null && TenantContext.get().containsModule(jobClassVo.getModuleId())) {
                jobHandler.initJob(tenantUuid);
            }
        }
        schedulerMapper.deleteUnusedJobStatus();
        JobLoadVo jobLoadVo = new JobLoadVo();
        jobLoadVo.setServerId(Config.SCHEDULE_SERVER_ID);
        jobLoadVo.setServerStartTime(HeartbeatManager.START_TIME);
        schedulerMapper.deleteJobLoadByServerIdAndServerStartTime(jobLoadVo);
    }

    @Override
    public void onInitialized(NeatLogicWebApplicationContext context) {
        Map<String, IJob> myMap = context.getBeansOfType(IJob.class);
        List<IJob> tmpJobHandlerList = new ArrayList<>();
        for (Map.Entry<String, IJob> entry : myMap.entrySet()) {
            IJob job = entry.getValue();
            tmpJobHandlerList.add(job);
            jobHandlerMap.put(job.getClassName(), job);
            JobClassVo jobClassVo = new JobClassVo(job.getClassName(), context.getId());
            jobClassVo.setName(job.getName());
            jobClassMap.put(job.getClassName(), jobClassVo);
            jobClassList.add(jobClassVo);
            // 如果定时作业组件没有实现IPublicJob接口，不会插入schedule_job_class表
            if (job instanceof IPublicJob) {
                jobClassVo.setType(JobClassVo.PUBLIC);
                publicJobClassList.add(jobClassVo);
            } else {
                jobClassVo.setType(JobClassVo.PRIVATE);
            }
        }
        if (CollectionUtils.isNotEmpty(tmpJobHandlerList)) {
            System.out.println("⚡" + $.t("common.startloadschedulejob", context.getModuleId()));
            for (TenantVo tenantVo : tenantList) {
                TenantContext.get().switchTenant(tenantVo.getUuid());//.setUseMasterDatabase(false);
                List<ModuleGroupVo> activeModuleGroupList = TenantContext.get().getActiveModuleGroupList();
                //TenantContext.get().switchTenant(tenantVo.getUuid()).setUseMasterDatabase(true);
                if (activeModuleGroupList.stream().map(ModuleGroupVo::getGroup).collect(Collectors.toList()).contains(context.getGroup())) {
                    CachedThreadPool.execute(new ScheduleLoadJobRunner(tenantVo.getUuid(), tmpJobHandlerList));
                    System.out.println("  ✓" + tenantVo.getName());
                }
            }
        }
    }

    class ScheduleLoadJobRunner extends NeatLogicThread {

        private final String tenantUuid;
        private final List<IJob> jobHandlerList;

        public ScheduleLoadJobRunner(String _tenantUuid, List<IJob> _jobHandlerList) {
            super("SCHEDULE-JOB-LOADER-" + _tenantUuid);
            tenantUuid = _tenantUuid;
            jobHandlerList = _jobHandlerList;
        }

        @Override
        protected void execute() {
            String oldThreadName = Thread.currentThread().getName();
            try {
                // 切换租户数据源
                TenantContext.get().switchTenant(tenantUuid);
                //可能存在误删，先注释
                //schedulerMapper.deleteJobLockByServerId(Config.SCHEDULE_SERVER_ID);
                UserContext.init(SystemUser.SYSTEM);
                for (IJob jobHandler : jobHandlerList) {
                    jobHandler.initJob(tenantUuid);
                }
                schedulerMapper.deleteUnusedJobStatus();
                JobLoadVo jobLoadVo = new JobLoadVo();
                jobLoadVo.setServerId(Config.SCHEDULE_SERVER_ID);
                jobLoadVo.setServerStartTime(HeartbeatManager.START_TIME);
                schedulerMapper.deleteJobLoadByServerIdAndServerStartTime(jobLoadVo);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            } finally {
                Thread.currentThread().setName(oldThreadName);
            }
        }
    }

}
