/*Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.*/

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
