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

package neatlogic.framework.config;

import neatlogic.framework.common.constvalue.ApiParamType;

/**
 * 租户级别配置接口
 */
public interface ITenantConfig {
    /**
     * 变量
     * @return
     */
    String getKey();
    /**
     * 默认值
     * @return
     */
    String getValue();
    /**
     * 描述
     * @return
     */
    String getDescription();

    /**
     * 数值类型
     * @return
     */
    default ApiParamType getType() {
        return null;
    }

    /**
     * 所属模块组
     * @return
     */
    default String getModuleGroup() {
        return null;
    }
}
