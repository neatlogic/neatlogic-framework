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
