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

package neatlogic.framework.scheduler.core;

import neatlogic.framework.scheduler.annotation.Param;
import neatlogic.framework.scheduler.dto.JobObject;
import neatlogic.framework.scheduler.dto.JobPropVo;
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
