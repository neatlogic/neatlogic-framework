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

import neatlogic.framework.asynchronization.threadpool.ScheduledThreadPool;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 参数重复校验
 */
public class SubmitKeyManager {
    private static final Map<String, Integer> SUBMIT_MAP = new ConcurrentHashMap<>();

    public static void add(String key, int timeout) {
        SUBMIT_MAP.put(key, timeout);
        ScheduledThreadPool.execute(new RemoveSubmitKeyThread(key) {
            @Override
            protected void execute() {
                SUBMIT_MAP.remove(this.getKey());
            }
        }, timeout);
    }

    public static boolean contain(String key) {
        return SUBMIT_MAP.containsKey(key);
    }

    public static void remove(String key) {
        SUBMIT_MAP.remove(key);
    }
}
