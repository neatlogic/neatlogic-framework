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
