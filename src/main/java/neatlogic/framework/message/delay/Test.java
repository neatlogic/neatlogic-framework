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

package neatlogic.framework.message.delay;

import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.asynchronization.threadpool.CachedThreadPool;
import neatlogic.framework.notify.dto.NotifyVo;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Test {

    /**
     * 租户列表
     **/
    private static List<String> tenantUuidList = Arrays.asList("tenant1", "tenant2", "tenant3", "tenant4");

    private static int count = 100000;

    private static ConcurrentMap<String, ConcurrentMap<NotifyVo, Object>> resultMap = new ConcurrentHashMap<>();

    public static void putAllNotifyVoMap(ConcurrentMap<NotifyVo, Object> _notifyVoMap) {
        ConcurrentMap<NotifyVo, Object> notifyVoMap = resultMap.get(TenantContext.get().getTenantUuid());
        if (notifyVoMap == null) {
            synchronized (Test.class) {
                notifyVoMap = resultMap.get(TenantContext.get().getTenantUuid());
                if (notifyVoMap == null) {
                    resultMap.put(TenantContext.get().getTenantUuid(), _notifyVoMap);
                    return;
                }
            }
        }
        notifyVoMap.putAll(_notifyVoMap);
    }

    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < tenantUuidList.size() * count; i++) {
            Thread.sleep(1);
            String tenantUuid = tenantUuidList.get(i % tenantUuidList.size());
            TenantContext.init(tenantUuid);
            CachedThreadPool.execute(new MessageProducerThread());
        }
        while (true) {
            if (CachedThreadPool.getThreadActiveCount() == 0) {
                break;
            }
        }
        for (Map.Entry<String, ConcurrentMap<NotifyVo, Object>> entry : resultMap.entrySet()) {
            String tenantUuid = entry.getKey();
            ConcurrentMap<NotifyVo, Object> notifyVoMap = entry.getValue();
            System.out.println(tenantUuid + ":" + notifyVoMap.size());
//            for (NotifyVo notifyVo : notifyVoMap.keySet()) {
//                if (!tenantUuid.equals(notifyVo.getTenantUuid())) {
//                    System.out.println(tenantUuid + ":" + notifyVo.getTenantUuid());
//                }
//            }
        }
        System.out.println("end...");
    }
}
