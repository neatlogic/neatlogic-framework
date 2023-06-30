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

package neatlogic.framework.util.jsondiff.core.config;

import neatlogic.framework.util.jsondiff.common.model.JsonComparedOption;
import neatlogic.framework.util.jsondiff.core.utils.JsonNeatFactory;

public class JsonDiffOption {

    /**
     * 全局配置
     */
    private static JsonComparedOption globallyUniqueOption = new JsonComparedOption();

    /**
     * 是否使用全局配置
     */
    private static boolean uniqueOption = false;

    /**
     * 默认的比较器工厂
     */
    private final static JsonNeatFactory jsonNeatFactory = new JsonNeatFactory();

    public static JsonNeatFactory getJsonNeatFactory() {
        return jsonNeatFactory;
    }


    public static JsonComparedOption getGloballyUniqueOption() {
        if (globallyUniqueOption == null) {
            return new JsonComparedOption();
        }
        return globallyUniqueOption;
    }

    public static void setGloballyUniqueOption(JsonComparedOption globallyUniqueOption) {
        JsonDiffOption.globallyUniqueOption = globallyUniqueOption;
    }

    public static boolean isUniqueOption() {
        return uniqueOption;
    }

    public static void openUniqueOption() {
        JsonDiffOption.uniqueOption = true;
    }

    public static void closeUniqueOption() {
        JsonDiffOption.uniqueOption = false;
    }
}
