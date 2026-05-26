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

    List<JobVo> getJobByHandler(String handler);

    List<JobVo> getJobByHandlerAndSourceServerIdList(@Param("handler") String handler, @Param("sourceServerIdList") List<Integer> sourceServerIdList);

    int searchJobCount(JobVo jobVo);

    List<JobVo> searchJob(JobVo jobVo);

    int searchJobAuditCount(JobAuditVo jobAuditVo);

    List<JobAuditVo> searchJobAudit(JobAuditVo jobAuditVo);

    JobAuditVo getJobAuditById(Long auditId);

    List<JobAuditVo> getJobAuditByUuid(String uuid);

    JobLockVo getJobLockByJobNameGroup(@Param("jobName") String jobName, @Param("jobGroup") String jobGroup);

    List<JobLockVo> getJobLockByServerId(Integer serverId);

    String getJobLastExecAuditStartTime(@Param("jobUuid") String jobUuid, @Param("status") String status);

    List<JobDataVo> getJobData(JobDataVo jobDataVo);


    int checkJobNameIsExists(JobVo job);

    List<JobLoadVo> getJobLoadListByJobNameGroup(@Param("jobName") String jobName, @Param("jobGroup") String jobGroup);

//    JobLoadTimeVo getJobLoadTime(JobLoadTimeVo jobLoadTimeVo);

    // UPDATE
    int updateJob(JobVo jobVo);

    int resetJobLockByServerId(Integer serverId);

    int updateJobStatus(JobStatusVo jobStatus);

    int updateJobNextFireTime(JobStatusVo jobStatus);

    int updateJobAudit(JobAuditVo scheduleJobAudit);

    int updateJobLock(JobLockVo jobLock);

    // INSERT
    int insertJob(JobVo job);

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

//    int deleteJobLoadTime(JobLoadTimeVo jobLoadTimeVo);
}
