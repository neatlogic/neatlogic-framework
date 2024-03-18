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

package neatlogic.framework.scheduler.dao.mapper;

import neatlogic.framework.scheduler.dto.*;
import org.apache.ibatis.annotations.Param;

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

    String getJobLastExecAuditStartTime(@Param("jobUuid") String jobUuid , @Param("status") String status);

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
