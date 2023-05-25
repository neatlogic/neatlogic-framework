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
