/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.scheduler.dao.mapper;

import codedriver.framework.scheduler.dto.*;
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

    JobLoadTimeVo getJobLoadTime(JobLoadTimeVo jobLoadTimeVo);

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

    int insertJobLoadTime(JobLoadTimeVo jobLoadTimeVo);

    // DELETE
    int deleteJobByUuid(String uuid);

    int deleteJobPropByJobUuid(String jobUuid);

    int deleteJobAuditByJobUuid(String jobUuid);

    int deleteJobStatus(@Param("jobName") String jobName, @Param("jobGroup") String jobGroup);

    int deleteJobLock(@Param("jobName") String jobName, @Param("jobGroup") String jobGroup);

    void deleteUnusedJobStatus();

    void deleteAuditByDayBefore(int dayBefore);

    int deleteJobLoadTime(JobLoadTimeVo jobLoadTimeVo);
}
