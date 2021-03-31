/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.cache.core;

import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.cache.annotation.MCache;
import codedriver.framework.cache.threadlocal.CacheContext;
import codedriver.framework.common.RootComponent;
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
