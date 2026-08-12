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

package neatlogic.framework.scheduler.dao.mapper;

import neatlogic.framework.scheduler.dto.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SchedulerMapper {
    // SELECT
    int checkJobAuditDetailIsExists(String hash);

    JobVo getJobByUuid(String uuid);

    JobVo getJobBaseInfoByUuid(String uuid);

    JobStatusVo getJobStatusByJobNameGroup(@Param("jobName") String jobName, @Param("jobGroup") String jobGroup, @Param("timestamp") Long timestamp);

//    List<JobStatusVo> getAllJobStatusList();

    List<JobVo> getJobByHandler(String handler);

    int searchJobCount(JobVo jobVo);

    List<JobVo> searchJob(JobVo jobVo);

    int searchJobAuditCount(JobAuditVo jobAuditVo);

    List<JobAuditVo> searchJobAudit(JobAuditVo jobAuditVo);

    JobAuditVo getJobAuditById(Long auditId);

    List<JobAuditVo> getJobAuditByUuid(String uuid);

    JobLockVo getJobLockByJobNameGroup(@Param("jobName") String jobName, @Param("jobGroup") String jobGroup);

    List<JobLockVo> getJobLockByServerId(Integer serverId);

    List<JobLockVo> getAllJobLockList();

    String getJobLastExecAuditStartTime(@Param("jobUuid") String jobUuid, @Param("status") String status);

    List<JobDataVo> getJobData(JobDataVo jobDataVo);


    int checkJobNameIsExists(JobVo job);

    List<JobLoadVo> getJobLoadListByJobNameGroup(@Param("jobName") String jobName, @Param("jobGroup") String jobGroup);

//    List<JobLoadVo> getAllJobLoadList();

    ScheduleJobSourceVo getJobSourceByJobNameAndJobGroup(@Param("jobName") String jobName, @Param("jobGroup") String jobGroup);

    /** 查询作业来源总数，供管理页分页使用。 */
//    int searchJobSourceCount(ScheduleJobSourceSearchVo searchVo);

    /** 查询作业来源列表，并补充作业名称和处理器信息。 */
//    List<ScheduleJobSourceVo> searchJobSource(ScheduleJobSourceSearchVo searchVo);

    List<ScheduleJobSourceVo> getAllJobSourceList();

    /** 查询已有服务器组，供管理页编辑控件复用。 */
//    List<String> getJobSourceServerGroupList();

//    JobLoadTimeVo getJobLoadTime(JobLoadTimeVo jobLoadTimeVo);

    // UPDATE
    int updateJob(JobVo jobVo);

    int resetJobLockByServerId(Integer serverId);

    int updateJobStatus(JobStatusVo jobStatus);

    int updateJobNextFireTime(JobStatusVo jobStatus);

    int updateJobAudit(JobAuditVo scheduleJobAudit);

    int updateJobLock(JobLockVo jobLock);

    /** 按作业来源复合主键批量更新服务器组。 */
//    int updateJobSourceServerGroup(@Param("jobSourceList") List<ScheduleJobSourceVo> jobSourceList,
//                                   @Param("serverGroup") String serverGroup);

    // INSERT
    int insertJob(JobVo job);

    int insertJobSource(ScheduleJobSourceVo jobSource);

    int insertJobSourceList(List<ScheduleJobSourceVo> jobSourceList);

    int insertJobStatus(JobStatusVo jobStatus);

    int insertJobProp(JobPropVo jobProp);

    int insertJobAudit(JobAuditVo scheduleJobAudit);

    int insertJobLock(JobLockVo jobLock);

    void saveJobData(JobDataVo jobDataVo);

    int insertJobAuditDetail(@Param("hash") String hash, @Param("content") String content);

    int insertJobLoad(JobLoadVo jobLoadVo);

//    int insertJobLoadTime(JobLoadTimeVo jobLoadTimeVo);

    // DELETE
    int deleteJobByUuid(String uuid);

    int deleteJobSourceByJobNameAndJobGroup(@Param("jobName") String jobName, @Param("jobGroup") String jobGroup);

    int deleteJobPropByJobUuid(String jobUuid);

    int deleteJobAuditByJobUuid(String jobUuid);

    int deleteJobStatus(@Param("jobName") String jobName, @Param("jobGroup") String jobGroup);

    int deleteJobLock(@Param("jobName") String jobName, @Param("jobGroup") String jobGroup);

    int deleteJobLockByServerId(Integer serverId);

    void deleteUnusedJobStatus();

    void deleteAuditDetail();

    void deleteAuditByDayBefore(int dayBefore);

    int deleteJobLoad(JobLoadVo jobLoadVo);

    int deleteJobLoadByServerId(int serverId);

    int deleteJobLoadByServerIdAndServerStartTime(JobLoadVo jobLoadVo);

    /**
     * 清空当前租户库中的作业加载记录。
     * 仅用于租户运行时卸载，不删除作业业务定义表schedule_job。
     *
     * @return 删除行数
     */
    int deleteAllJobLoad();

    /**
     * 清空当前租户库中的作业状态记录。
     * 仅用于租户运行时卸载，不删除作业业务定义表schedule_job。
     *
     * @return 删除行数
     */
    int deleteAllJobStatus();

    /**
     * 清空当前租户库中的作业锁记录。
     * 仅用于租户运行时卸载，不删除作业业务定义表schedule_job。
     *
     * @return 删除行数
     */
    int deleteAllJobLock();

//    int deleteJobLoadTime(JobLoadTimeVo jobLoadTimeVo);
}
