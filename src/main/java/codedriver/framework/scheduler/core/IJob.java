/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.scheduler.core;

import codedriver.framework.scheduler.annotation.Param;
import codedriver.framework.scheduler.dto.JobObject;
import codedriver.framework.scheduler.dto.JobPropVo;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.util.ClassUtils;

import java.util.List;
import java.util.Map;

public interface IJob extends Job {

    void executeInternal(JobExecutionContext context, JobObject jobObject) throws JobExecutionException;

    /**
     * 模块全路径
     *
     * @return 类路径
     */
    default String getClassName() {
        return ClassUtils.getUserClass(this.getClass()).getName();
    }

    default IJob getThis() {
        return SchedulerManager.getHandler(this.getClassName());
    }

    /**
     * 获取分组名称
     *
     * @return 分组名称
     */
    String getGroupName();

    /**
     * 解析注解参数
     *
     * @return 注解map
     */
    Map<String, Param> initProp();

    /**
     * 参数类型校验
     *
     * @param jobPropVoList 作业信息
     * @return 是否通过校验
     */
    Boolean valid(List<JobPropVo> jobPropVoList);

    /**
     * 检查作业表达式是否已经过时
     *
     * @param jobObject 作业信息
     * @return 是或否
     */
    Boolean checkCronIsExpired(JobObject jobObject);

    /**
     * 重新加载单个作业
     *
     * @param jobObject 作业信息
     */
    void reloadJob(JobObject jobObject);

    /**
     * 加载当前类的租户作业
     *
     * @param tenantUuid 租户uuid
     */
    void initJob(String tenantUuid);

    /**
     * 是否启用审计，默认启用
     *
     * @return 是否需要审计
     */
    default Boolean isAudit() {
        return true;
    }
}
