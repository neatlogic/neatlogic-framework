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

package neatlogic.framework.cache.threadlocal;

import java.util.HashMap;
import java.util.Map;

public class CacheContext {
    private static final transient ThreadLocal<Map<String, Object>> instance = new ThreadLocal<>();


    public static Map<String, Object> get() {
        return instance.get();
    }

    /**
     * 添加数据
     *
     * @param key   类+方法名+参数
     * @param value 值
     */
    public static void putData(String key, Object value) {
        if (value != null) {
            if (instance.get() == null) {
                instance.set(new HashMap<>());
            }
            Map<String, Object> cacheObj = instance.get();
            cacheObj.put(key, value);
        }
    }

    /**
     * 获取数据
     *
     * @param key 类+方法名+参数
     * @return 值
     */
    public static Object getData(String key) {
        if (instance.get() != null) {
            return instance.get().get(key);
        }
        return null;
    }


    public static void release() {
        instance.remove();
    }
}
