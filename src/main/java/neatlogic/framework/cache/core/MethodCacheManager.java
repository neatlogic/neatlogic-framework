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
