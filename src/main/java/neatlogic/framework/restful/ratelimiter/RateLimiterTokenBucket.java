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

package neatlogic.framework.restful.ratelimiter;

import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.config.ConfigManager;
import neatlogic.framework.config.FrameworkTenantConfig;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Objects;

@Component
public class RateLimiterTokenBucket {

    private static final SoftReferenceCache<TenantRateLimiter> tenantRateLimiterMap = new SoftReferenceCache<>(new HashMap<>());

    /**
     * 尝试获取令牌
     * @return
     */
    public static boolean tryAcquire() {
        Double apiQPS = null;
        String apiQPSStr = ConfigManager.getConfig(FrameworkTenantConfig.API_QPS);
        if (StringUtils.isNotBlank(apiQPSStr)) {
            apiQPS = new Double(apiQPSStr);
        }
        String tenantUuid = TenantContext.get().getTenantUuid();
        TenantRateLimiter tenantRateLimiter = tenantRateLimiterMap.get(tenantUuid);
        //如果这是租户的第一次请求，则新建一个租户限速器对象
        if (tenantRateLimiter == null) {
            synchronized (RateLimiterTokenBucket.class) {
                tenantRateLimiter = tenantRateLimiterMap.get(tenantUuid);
                if (tenantRateLimiter == null) {
                    tenantRateLimiter = new TenantRateLimiter(apiQPS);
                    tenantRateLimiterMap.put(tenantUuid, tenantRateLimiter);
                }
            }
        } else {
            if (!Objects.equals(tenantRateLimiter.getPermitsPerSecond(), apiQPS)) {
                tenantRateLimiter.setPermitsPerSecond(apiQPS);
            }
        }
        return tenantRateLimiter.tryAcquire();
    }

    /**
     * 移除指定租户的限流桶。
     * 租户禁用、删除后不保留旧租户的限流状态。
     *
     * @param tenantUuid 租户uuid
     */
    public static void removeTenant(String tenantUuid) {
        if (StringUtils.isNotBlank(tenantUuid)) {
            tenantRateLimiterMap.remove(tenantUuid);
        }
    }

//    // -Xms10m -Xmx10m -XX:+PrintGCDetails
//    public static void main(String[] args) {
//        List<String> tenantUuidList = Arrays.asList("develop", "test", "szbank");
//        Map<String, Double> tenantRateMap = new HashMap<>();
//        tenantRateMap.put("develop", 10.0);
//        tenantRateMap.put("test", 20.0);
//        tenantRateMap.put("szbank", 30.0);
//        for (int i = 0; i < 100; i++) {
//            new Thread(() -> {
//                for (int j = 0; j < 12; j++) {
//                    String tenantUuid = tenantUuidList.get(j % tenantUuidList.size());
//                    TenantContext.init(tenantUuid);
//                    RequestContext requestContext = RequestContext.init(null, "a");
//                    String name = Thread.currentThread().getName();
//                    double nameDouble = Double.parseDouble(name);
//                    nameDouble *= 100;
//                    requestContext.setRate(nameDouble + j);
//                    if(RateLimiterTokenBucket.tryAcquire()) {
////                        System.out.println(Thread.currentThread().getName() + "-" + tenantUuid + "-的第" + j + "次-成功");
//                    } else {
////                        System.out.println(Thread.currentThread().getName() + "-" + tenantUuid + "-的第" + j + "次-失败");
//                    }
//
////                    byte[] b = new byte[1024 * (4096-3580)];
////                    System.gc();
//                }
//            }, i + "").start();
//        }
//    }
}
