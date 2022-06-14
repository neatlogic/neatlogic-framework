/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.restful.ratelimiter;

import codedriver.framework.asynchronization.threadlocal.RequestContext;
import codedriver.framework.asynchronization.threadlocal.TenantContext;
import com.google.common.util.concurrent.RateLimiter;

import java.util.HashMap;
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
        RequestContext requestContext = RequestContext.get();
        requestContext.setTenantRate(permitsPerSecond);
        boolean flag = true;
        if (permitsPerSecond != null && permitsPerSecond.doubleValue() != 0 && rateLimiter != null) {
            flag = rateLimiter.tryAcquire(999, TimeUnit.MILLISECONDS);
        }
        if (!flag) {
            requestContext.setRejectSource("tenant");
            return false;
        }
        String token = requestContext.getUrl();
        Double rate = requestContext.getApiRate();
        if (rate == null || rate.doubleValue() == 0) {
            return true;
        }

        RateLimiter apiRateLimiter = apiRateLimiterMap.get(token);
        //如果这是接口的第一次请求，则新建一个接口限速器对象
        if (apiRateLimiter == null) {
            synchronized (this) {
                apiRateLimiter = apiRateLimiterMap.get(token);
                if (apiRateLimiter == null) {
                    apiRateLimiter = RateLimiter.create(rate.doubleValue());
                    apiRateLimiterMap.put(token, apiRateLimiter);
                }
            }
        } else {
            if (apiRateLimiter.getRate() != rate.doubleValue()) {
                synchronized (this) {
                    apiRateLimiter = apiRateLimiterMap.get(token);
                    if (apiRateLimiter.getRate() != rate.doubleValue()) {
                        apiRateLimiter.setRate(rate.doubleValue());
                    }
                }
            }
        }
        flag = apiRateLimiter.tryAcquire(999, TimeUnit.MILLISECONDS);
        if (!flag) {
            requestContext.setRejectSource("api");
        }
        return flag;
    }
}
