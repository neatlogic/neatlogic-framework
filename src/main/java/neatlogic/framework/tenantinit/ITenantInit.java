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

package neatlogic.framework.tenantinit;

public interface ITenantInit {

    /**
     * 作业名称
     *
     * @return 字符串
     */
    String getName();
    /**
     * 初始化租户的时候会执行
     */
    void execute();
    /**
     * 排序
     *
     * @return 顺序
     */
    int sort();

    /**
     * 设置所在模块组
     * @param group 模块组
     */
    void setGroup(String group);

    /**
     * 获取所在模块组
     * @return 模块组
     */
    String getGroup();

}
