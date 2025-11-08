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

package neatlogic.framework.util;

/**
 * 主要用于翻译封装字符串
 */
public class $ {
    /**
     * 翻译
     *
     * @param key 翻译的key
     * @return 翻译后的值
     */
    public static String t(String key) {
        return I18nUtils.getMessage(key);
    }

    /**
     * 翻译
     *
     * @param key  翻译的key
     * @param args key里面需要替换的变量
     * @return 翻译后的值
     */
    public static String t(String key, Object... args) {
        return I18nUtils.getMessage(key, args);
    }
}
