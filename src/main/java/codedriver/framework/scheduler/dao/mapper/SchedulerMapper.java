/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.scheduler.dao.mapper;

import codedriver.framework.scheduler.dto.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SchedulerMapper {
    // SELECT
    public JobVo getJobByUuid(String uuid);

    public JobVo getJobBaseInfoByUuid(String uuid);

    public JobStatusVo getJobStatusByJobNameGroup(@Param("jobName") String jobName, @Param("jobGroup") String jobGroup);

    public List<JobVo> getJobByHandler(String handler);

    public int searchJobCount(JobVo jobVo);

    public List<JobVo> searchJob(JobVo jobVo);

    public int searchJobAuditCount(JobAuditVo jobAuditVo);

    public List<JobAuditVo> searchJobAudit(JobAuditVo jobAuditVo);

    public JobAuditVo getJobAuditById(Long auditId);

    public List<JobAuditVo> getJobAuditByUuid(String uuid);

    public JobLockVo getJobLockByJobNameGroup(@Param("jobName") String jobName, @Param("jobGroup") String jobGroup);

    public List<JobLockVo> getJobLockByServerId(Integer serverId);

    public int checkJobNameIsExists(JobVo job);

    // UPDATE
    public int updateJob(JobVo jobVo);

    public int resetJobLockByServerId(Integer serverId);

    public int updateJobStatus(JobStatusVo jobStatus);

    public int updateJobNextFireTime(JobStatusVo jobStatus);

    public int updateJobAudit(JobAuditVo scheduleJobAudit);

    public int updateJobLock(JobLockVo jobLock);

    public int updateJobLockByServerId(JobLockVo jobLock);

    // INSERT
    public int insertJob(JobVo job);

    public int insertJobStatus(JobStatusVo jobStatus);

    public int insertJobProp(JobPropVo jobProp);

    public int insertJobAudit(JobAuditVo scheduleJobAudit);

    public int insertJobLock(JobLockVo jobLock);

    public int replaceJobAuditDetail(@Param("hash") String hash, @Param("content") String content);

    // DELETE
    public int deleteJobByUuid(String uuid);

    public int deleteJobPropByJobUuid(String jobUuid);

    public int deleteJobAuditByJobUuid(String jobUuid);

    public int deleteJobStatus(@Param("jobName") String jobName, @Param("jobGroup") String jobGroup);

    public int deleteJobLock(@Param("jobName") String jobName, @Param("jobGroup") String jobGroup);

    void deleteUnusedJobStatus();

}
