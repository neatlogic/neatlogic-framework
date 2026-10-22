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

/** 当前线程的自检调用上下文，供显式准入的目标 API 生成隔离测试数据。 */
public final class SelfCheckContextHolder {
    private static final ThreadLocal<SelfCheckContextVo> HOLDER = new ThreadLocal<>();

    private SelfCheckContextHolder() {
    }

    public static void set(SelfCheckContextVo contextVo) {
        HOLDER.set(contextVo);
    }

    public static SelfCheckContextVo get() {
        return HOLDER.get();
    }

    public static void clear() {
        HOLDER.remove();
    }
}
