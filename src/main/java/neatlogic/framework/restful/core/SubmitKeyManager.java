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

package neatlogic.framework.restful.core;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 参数重复校验
 * 定时作业SubmitKeyClearJob 凌晨3点清map
 */
public class SubmitKeyManager {
    private static final Map<String, Long> SUBMIT_MAP = new ConcurrentHashMap<>();

    /**
     * 添加一个 key，并设置 timeout 秒后过期
     */
    public static void add(String key, int timeout) {
        long expireTime = System.currentTimeMillis() + timeout * 1000L;
        SUBMIT_MAP.put(key, expireTime);
    }

    /**
     * 检查 key 是否存在（未过期）
     */
    public static boolean contain(String key) {
        Long expireTime = SUBMIT_MAP.get(key);
        if (expireTime == null) {
            return false;
        }
        if (System.currentTimeMillis() > expireTime) {
            SUBMIT_MAP.remove(key); // 懒移除
            return false;
        }
        return true;
    }

    /**
     * 手动移除 key
     */
    public static void remove(String key) {
        SUBMIT_MAP.remove(key);
    }

    public static void clear() {
        SUBMIT_MAP.clear();
    }

    public static Map<String, Long> getAll() {
        return new HashMap<>(SUBMIT_MAP); // 防止外部修改原始 map
    }
}
