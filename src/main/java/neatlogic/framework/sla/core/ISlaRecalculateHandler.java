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

package neatlogic.framework.sla.core;

import org.springframework.util.ClassUtils;

/**
 * SLA重算接口，当服务窗口排班更新时，调用该接口的实现类进行相关SLA耗时重算
 */
public interface ISlaRecalculateHandler {

    default String getHandler() {
        return ClassUtils.getUserClass(this.getClass()).getName();
    }

    /**
     * 根据服务窗口uuid重新计算相关的时效
     * @param worktimeUuid 重新排班的服务窗口uuid
     */
    void execute(String worktimeUuid);
}
