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

package neatlogic.framework.cache.core;

import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.cache.annotation.MCache;
import neatlogic.framework.cache.threadlocal.CacheContext;
import neatlogic.framework.common.RootComponent;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;

@Aspect
@RootComponent
public class MethodCacheManager {
    @Around(value = "@annotation(mCache)")
    public Object createFullIndex(ProceedingJoinPoint joinPoint, MCache mCache) throws Throwable {
        Object[] args = joinPoint.getArgs();
        Object target = joinPoint.getTarget().getClass().getName();
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        StringBuilder sb = new StringBuilder();
        sb.append(TenantContext.get().getTenantUuid()).append("_");
        sb.append(target).append(".").append(methodSignature.getName());
        for (Object a : args) {
            sb.append("_").append(a.hashCode());
        }
        String key = DigestUtils.md5DigestAsHex(sb.toString().getBytes(StandardCharsets.UTF_8));
        Object returnValue = CacheContext.getData(key);
        if (returnValue == null) {
            returnValue = joinPoint.proceed(args);
            CacheContext.putData(key, returnValue);
        }
        return returnValue;
    }
}
