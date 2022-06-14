/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.tokenratelimiter;

import codedriver.framework.asynchronization.threadlocal.RequestContext;
import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.dao.mapper.ConfigMapper;
import codedriver.framework.dto.ConfigVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

@Component
public class RateLimiterTokenBucket {

    private static final SoftReferenceCache<TenantRateLimiter> tenantRateLimiterMap = new SoftReferenceCache<>(new HashMap<>());

    private static ConfigMapper configMapper;

    @Resource
    public void setConfigMapper(ConfigMapper _configMapper) {
        configMapper = _configMapper;
    }
    /**
     * 尝试获取令牌
     * @return
     */
    public static boolean tryAcquire() {
        Double apiQPS = null;
        ConfigVo configVo = configMapper.getConfigByKey("apiqps");
        if (configVo != null) {
            String value = configVo.getValue();
            if (StringUtils.isNotBlank(value)) {
                apiQPS = new Double(value);
            }
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
                    System.out.println(Thread.currentThread() + "-新建租户限速器：" + tenantUuid + "-" + tenantRateLimiter.getPermitsPerSecond());
                }
            }
        } else {
            if (!Objects.equals(tenantRateLimiter.getPermitsPerSecond(), apiQPS)) {
                tenantRateLimiter.setPermitsPerSecond(apiQPS);
            }
        }
        return tenantRateLimiter.tryAcquire();
    }

    // -Xms10m -Xmx10m -XX:+PrintGCDetails
    public static void main(String[] args) {
        List<String> tenantUuidList = Arrays.asList("develop", "test", "szbank");
        Map<String, Double> tenantRateMap = new HashMap<>();
        tenantRateMap.put("develop", 10.0);
        tenantRateMap.put("test", 20.0);
        tenantRateMap.put("szbank", 30.0);
        for (int i = 0; i < 100; i++) {
            new Thread(() -> {
                for (int j = 0; j < 12; j++) {
                    String tenantUuid = tenantUuidList.get(j % tenantUuidList.size());
                    TenantContext.init(tenantUuid);
                    RequestContext requestContext = RequestContext.init(null, "a");
                    String name = Thread.currentThread().getName();
                    double nameDouble = Double.parseDouble(name);
                    nameDouble *= 100;
                    requestContext.setRate(nameDouble + j);
                    if(RateLimiterTokenBucket.tryAcquire()) {
//                        System.out.println(Thread.currentThread().getName() + "-" + tenantUuid + "-的第" + j + "次-成功");
                    } else {
//                        System.out.println(Thread.currentThread().getName() + "-" + tenantUuid + "-的第" + j + "次-失败");
                    }

//                    byte[] b = new byte[1024 * (4096-3580)];
//                    System.gc();
                }
            }, i + "").start();
        }
    }
}
