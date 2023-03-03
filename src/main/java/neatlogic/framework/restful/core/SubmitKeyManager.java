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
