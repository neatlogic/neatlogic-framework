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

package neatlogic.framework.selfcheck.core;

import neatlogic.framework.selfcheck.dto.SelfCheckContextVo;

/** 写入型自检 API 必须实现的幂等清理契约。 */
public interface ISelfCheckCleanupApi {
    /**
     * 清理本次自检创建的数据。
     * 实现必须只按本次 runItemId 等唯一标识删除，并允许重复调用。
     */
    void cleanup(SelfCheckContextVo contextVo);
}
