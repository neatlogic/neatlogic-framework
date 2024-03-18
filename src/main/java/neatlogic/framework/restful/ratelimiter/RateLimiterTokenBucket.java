/*Copyright (C) $today.year  深圳极向量科技有限公司 All Rights Reserved.

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
