/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.message.delay;

import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.asynchronization.threadpool.CachedThreadPool;
import codedriver.framework.notify.dto.NotifyVo;

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
