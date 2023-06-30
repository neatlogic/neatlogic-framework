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
