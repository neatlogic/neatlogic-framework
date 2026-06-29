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

import neatlogic.framework.scheduler.annotation.Param;
import neatlogic.framework.scheduler.dto.JobObject;
import neatlogic.framework.scheduler.dto.JobPropVo;
import neatlogic.framework.scheduler.dto.JobVo;
import neatlogic.framework.scheduler.enums.JobLoadTriggerType;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.util.ClassUtils;

import java.util.List;
import java.util.Map;

public interface IJob extends Job {

    void executeInternal(JobExecutionContext context, JobObject jobObject) throws Exception;

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
     * 获取定时作业组件名称
     *
     * @return 定时作业组件名称
     */
    String getName();

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
     * 检查作业是否正常，不正常的作业需要重新加载
     *
     * @param jobObject 作业信息
     * @return 是或否
     */
    Boolean isHealthy(JobObject jobObject);

    /**
     * 重新加载单个作业
     *
     * @param jobObject 作业信息
     */
    default void reloadJob(JobObject jobObject) {
        reloadJob(jobObject, JobLoadTriggerType.DO_NOTHING);
        // TODO 为了避免有些没有修改的模块报错，暂时增加这个默认方法
    }

    /**
     * 重新加载单个作业
     *
     * @param jobObject 作业信息
     * @param triggerType 触发点
     */
    default void reloadJob(JobObject jobObject, JobLoadTriggerType triggerType) {
        // TODO 为了避免有些没有修改的模块报错，暂时增加这个默认方法
    }

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

    /**
     * 根据uuid到数据库对应表获取对应作业
     *
     * @param uuid 唯一标识
     */
    default JobVo getJob(String uuid) throws Exception {
        return null;
    }

    /**
     * 默认内部作业private
     */
    default String getType() {
        return "private";
    }
}
