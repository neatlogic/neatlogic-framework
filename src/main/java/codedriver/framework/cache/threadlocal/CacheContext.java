/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.cache.threadlocal;

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
