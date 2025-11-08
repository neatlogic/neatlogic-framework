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
