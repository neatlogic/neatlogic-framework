package neatlogic.framework.dao.aop;

import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.common.RootComponent;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;


@Aspect
@RootComponent
public class SwitchMasterDatabaseAspect {
    @Around("@annotation(neatlogic.framework.dao.aop.UseMasterDatabase) || @within(neatlogic.framework.dao.aop.UseMasterDatabase)")
    public Object switchMasterDatabase(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            TenantContext.get().setUseMasterDatabase(true);
            return joinPoint.proceed();
        } finally {
            TenantContext.get().setUseMasterDatabase(false);
        }
    }

}
