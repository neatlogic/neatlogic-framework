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

package neatlogic.framework.startup;

public interface IStartup {
    /**
     * 作业名称
     *
     * @return 字符串
     */
    String getName();

    /**
     * 每个租户分别执行
     */
    void executeForCurrentTenant();

    /*
    只执行一次，晚于executeForTenant执行
     */
    void executeForAllTenant();

    /**
     * 排序
     *
     * @return 顺序
     */
    int sort();

    /**
     * 设置所在模块组名
     * @param group 模块组
     */
    void setGroup(String group);

    /**
     * 获取所在模块组
     * @return  模块组
     */
    String getGroup();
}
