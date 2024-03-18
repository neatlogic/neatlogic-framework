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

package neatlogic.framework.restful.ratelimiter;

import com.google.common.util.concurrent.RateLimiter;
import neatlogic.framework.asynchronization.threadlocal.RequestContext;
import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.restful.constvalue.RejectSource;

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
        if (permitsPerSecond != null && permitsPerSecond != 0) {
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
        if (permitsPerSecond != null && permitsPerSecond != 0) {
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
        if (permitsPerSecond != null && permitsPerSecond != 0 && rateLimiter != null) {
            flag = rateLimiter.tryAcquire(999, TimeUnit.MILLISECONDS);
        }
        if (!flag) {
            requestContext.setRejectSource(RejectSource.TENANT);
            return false;
        }
        String token = requestContext.getUrl();
        Double rate = requestContext.getApiRate();
        if (rate == null || rate == 0) {
            return true;
        }

        RateLimiter apiRateLimiter = apiRateLimiterMap.get(token);
        //如果这是接口的第一次请求，则新建一个接口限速器对象
        if (apiRateLimiter == null) {
            synchronized (this) {
                apiRateLimiter = apiRateLimiterMap.get(token);
                if (apiRateLimiter == null) {
                    apiRateLimiter = RateLimiter.create(rate);
                    apiRateLimiterMap.put(token, apiRateLimiter);
                }
            }
        } else {
            if (apiRateLimiter.getRate() != rate.doubleValue()) {
                synchronized (this) {
                    apiRateLimiter = apiRateLimiterMap.get(token);
                    if (apiRateLimiter.getRate() != rate) {
                        apiRateLimiter.setRate(rate.doubleValue());
                    }
                }
            }
        }
        flag = apiRateLimiter.tryAcquire(999, TimeUnit.MILLISECONDS);
        if (!flag) {
            requestContext.setRejectSource(RejectSource.API);
        }
        return flag;
    }
}
