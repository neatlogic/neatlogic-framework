/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.tokenratelimiter;

import codedriver.framework.asynchronization.threadlocal.RequestContext;
import codedriver.framework.asynchronization.threadlocal.TenantContext;
import com.google.common.util.concurrent.RateLimiter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

public class TenantRateLimiter {

    private final String tenantUuid;
    private final RateLimiter rateLimiter;
    private final SoftReferenceCache<RateLimiter> apiRateLimiterMap = new SoftReferenceCache<>(new HashMap<>());
    private Double permitsPerSecond;
    public TenantRateLimiter(Double permitsPerSecond) {
        this.permitsPerSecond = permitsPerSecond;
        this.tenantUuid = TenantContext.get().getTenantUuid();
        if (permitsPerSecond != null && permitsPerSecond.doubleValue() != 0) {
            this.rateLimiter = RateLimiter.create(permitsPerSecond);
        } else {
            this.rateLimiter = null;
        }
    }

    public String getTenantUuid() {
        return tenantUuid;
    }

    public RateLimiter getRateLimiter() {
        return rateLimiter;
    }

    public Double getPermitsPerSecond() {
        return permitsPerSecond;
    }

    public void setPermitsPerSecond(Double permitsPerSecond) {
        this.permitsPerSecond = permitsPerSecond;
        if (permitsPerSecond != null && permitsPerSecond.doubleValue() != 0) {
            this.rateLimiter.setRate(permitsPerSecond);
        }
    }

    /**
     * 尝试获取令牌，先从租户级别的限速器获取令牌，成功后再从接口级别的限速器获取令牌
     * @return
     */
    public boolean tryAcquire() {
        boolean flag = true;
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");
        Date start = null;
        Date end = null;
        double rate2 = 0L;
        if (permitsPerSecond != null && permitsPerSecond.doubleValue() != 0 && rateLimiter != null) {
            start = new Date();
            flag = rateLimiter.tryAcquire(999, TimeUnit.MILLISECONDS);
            rate2 = rateLimiter.getRate();
            end = new Date();
        }
        if (!flag) {
//            System.out.println(Thread.currentThread() + "-" + tenantUuid + "租户级别-失败");
            return false;
        }
//        System.out.println(Thread.currentThread() + "-"+ rate2 + "-" + tenantUuid + "租户级别-成功-" + sdf.format(end) + "-" + sdf.format(start));
//        return true;
        RequestContext requestContext = RequestContext.get();
        String token = requestContext.getUrl();
        Double rate = requestContext.getRate();
        if (rate == null || rate.doubleValue() == 0) {
            System.out.println(Thread.currentThread() + "-" + tenantUuid + "-" + requestContext.getUrl() + "-rate=null");
            return true;
        }

//        System.out.println("newRate=" + rate.doubleValue());
        RateLimiter apiRateLimiter = apiRateLimiterMap.get(token);
        //如果这是接口的第一次请求，则新建一个接口限速器对象
        if (apiRateLimiter == null) {
            synchronized (this) {
                apiRateLimiter = apiRateLimiterMap.get(token);
                if (apiRateLimiter == null) {
                    apiRateLimiter = RateLimiter.create(rate.doubleValue());
                    apiRateLimiterMap.put(token, apiRateLimiter);
                    System.out.println(Thread.currentThread() + "-新建请求限速器：" + token + "-" + apiRateLimiter.getRate());
                }
            }
        } else {
//            System.out.println("oldRate=" + apiRateLimiter.getRate());
            if (apiRateLimiter.getRate() != rate.doubleValue()) {
//                System.out.println("oldRate!=newRate");
                synchronized (this) {
                    apiRateLimiter = apiRateLimiterMap.get(token);
                    if (apiRateLimiter.getRate() != rate.doubleValue()) {
                        System.out.println(Thread.currentThread() + "-"+ apiRateLimiter.getRate()+ "-"+ rate.doubleValue() + "-" + tenantUuid + "-" + token + "改变-" + sdf.format(new Date()));
                        apiRateLimiter.setRate(rate.doubleValue());
                    }
                }
            }
        }
        start = new Date();
        boolean result = apiRateLimiter.tryAcquire(999, TimeUnit.MILLISECONDS);
        end = new Date();
        if (result) {
//            System.out.println(Thread.currentThread() + "-"+ apiRateLimiter.getRate() + "-" + tenantUuid + "-" + token + "请求级别-成功-" + sdf.format(end) + "-" + sdf.format(start));
        }
        return result;
    }
}
