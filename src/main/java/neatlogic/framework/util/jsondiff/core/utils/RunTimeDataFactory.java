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

package neatlogic.framework.util.jsondiff.core.utils;


import neatlogic.framework.util.jsondiff.common.model.JsonComparedOption;
import neatlogic.framework.util.jsondiff.core.config.JsonDiffOption;

public class RunTimeDataFactory {


    /**
     * 配置
     */
    private final static ThreadLocal<JsonComparedOption> optionThreadLocal = new ThreadLocal<>();

    /**
     * 获取对比配置
     *
     * @return 对比配置
     */
    public static JsonComparedOption getOptionInstance() {
        if (JsonDiffOption.isUniqueOption()) {
            return JsonDiffOption.getGloballyUniqueOption();
        }
        if (optionThreadLocal.get() == null) {
            optionThreadLocal.set(new JsonComparedOption());
        }
        // 默认配置
        return optionThreadLocal.get();
    }

    /**
     * s设置对比配置
     *
     * @param jsonComparedOption 对比配置
     */
    public static void setOptionInstance(JsonComparedOption jsonComparedOption) {
        if (jsonComparedOption == null) {
            return;
        }
        optionThreadLocal.remove();
        optionThreadLocal.set(jsonComparedOption);
    }

    /**
     * 清理ThreadLocal
     */
    public static void clearOptionInstance() {
        optionThreadLocal.remove();
    }

}
