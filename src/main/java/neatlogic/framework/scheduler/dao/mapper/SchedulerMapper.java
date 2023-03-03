/*
 * Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package neatlogic.framework.scheduler.dao.mapper;

import neatlogic.framework.scheduler.dto.*;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface SchedulerMapper {
    // SELECT
    int checkJobAuditDetailIsExists(String hash);

    JobVo getJobByUuid(String uuid);

    JobVo getJobBaseInfoByUuid(String uuid);

    JobStatusVo getJobStatusByJobNameGroup(@Param("jobName") String jobName, @Param("jobGroup") String jobGroup);

    List<JobVo> getJobByHandler(String handler);

    int searchJobCount(JobVo jobVo);

    List<JobVo> searchJob(JobVo jobVo);

    int searchJobAuditCount(JobAuditVo jobAuditVo);

    List<JobAuditVo> searchJobAudit(JobAuditVo jobAuditVo);

    JobAuditVo getJobAuditById(Long auditId);

    List<JobAuditVo> getJobAuditByUuid(String uuid);

    JobLockVo getJobLockByJobNameGroup(@Param("jobName") String jobName, @Param("jobGroup") String jobGroup);

    List<JobLockVo> getJobLockByServerId(Integer serverId);

    int checkJobNameIsExists(JobVo job);

//    JobLoadTimeVo getJobLoadTime(JobLoadTimeVo jobLoadTimeVo);

    // UPDATE
    int updateJob(JobVo jobVo);

    int resetJobLockByServerId(Integer serverId);

    int updateJobStatus(JobStatusVo jobStatus);

    int updateJobNextFireTime(JobStatusVo jobStatus);

    int updateJobAudit(JobAuditVo scheduleJobAudit);

    int updateJobLock(JobLockVo jobLock);

    int updateJobLockByServerId(JobLockVo jobLock);

    // INSERT
    int insertJob(JobVo job);

    int insertJobStatus(JobStatusVo jobStatus);

    int insertJobProp(JobPropVo jobProp);

    int insertJobAudit(JobAuditVo scheduleJobAudit);

    int insertJobLock(JobLockVo jobLock);

    int insertJobAuditDetail(@Param("hash") String hash, @Param("content") String content);

//    int insertJobLoadTime(JobLoadTimeVo jobLoadTimeVo);

    // DELETE
    int deleteJobByUuid(String uuid);

    int deleteJobPropByJobUuid(String jobUuid);

    int deleteJobAuditByJobUuid(String jobUuid);

    int deleteJobStatus(@Param("jobName") String jobName, @Param("jobGroup") String jobGroup);

    int deleteJobLock(@Param("jobName") String jobName, @Param("jobGroup") String jobGroup);

    void deleteUnusedJobStatus();

    void deleteAuditByDayBefore(int dayBefore);

//    int deleteJobLoadTime(JobLoadTimeVo jobLoadTimeVo);
}
