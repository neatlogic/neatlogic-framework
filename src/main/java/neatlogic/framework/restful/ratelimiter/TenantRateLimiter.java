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

package neatlogic.framework.restful.ratelimiter;

import neatlogic.framework.asynchronization.threadlocal.RequestContext;
import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.restful.constvalue.RejectSource;
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
