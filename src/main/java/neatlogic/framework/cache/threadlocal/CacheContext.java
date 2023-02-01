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

package neatlogic.framework.cache.threadlocal;

import java.util.HashMap;
import java.util.Map;

public class CacheContext {
    private final transient static ThreadLocal<Map<String, Object>> instance = new ThreadLocal<>();


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
                instance.set(new HashMap<String, Object>());
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
