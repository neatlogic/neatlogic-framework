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

package neatlogic.framework.restful.core;

import neatlogic.framework.asynchronization.threadlocal.TenantContext;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 参数重复校验
 * GetSubmitKeyInfoApi 实时查看队列情况接口
 * CleanSubmitKeyApi 临时手动清理接口
 * 定时作业SubmitKeyClearJob 凌晨3点清map
 */
public class SubmitKeyManager {
    private static final Map<String, Long> SUBMIT_MAP = new ConcurrentHashMap<>();
    private static final int CLEANUP_THRESHOLD = 10000; // 清理阈值

    /**
     * 添加一个 key，并设置 timeout 秒后过期
     */
    public static void add(String key, int timeout) {
        long expireTime = System.currentTimeMillis() + timeout * 1000L;
        SUBMIT_MAP.put(TenantContext.get().getTenantUuid() + " " + key, expireTime);
        // 超过阈值触发清理
        if (SUBMIT_MAP.size() > CLEANUP_THRESHOLD) {
            cleanupExpiredKeys();
        }
    }

    /**
     * 检查 key 是否存在（未过期）
     */
    public static boolean contain(String key) {
        String keyTmp = TenantContext.get().getTenantUuid() + " " + key;
        Long expireTime = SUBMIT_MAP.get(keyTmp);
        if (expireTime == null) {
            return false;
        }
        if (System.currentTimeMillis() > expireTime) {
            SUBMIT_MAP.remove(keyTmp); // 懒移除
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

    public static void cleanupExpiredKeys() {
        long now = System.currentTimeMillis();
        SUBMIT_MAP.entrySet().removeIf(entry -> entry.getValue() < now);
    }
}
