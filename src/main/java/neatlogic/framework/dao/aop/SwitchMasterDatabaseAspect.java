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
            //System.out.println("切换MASTER：" + joinPoint.getSignature().getName());
            TenantContext.get().setUseMasterDatabase(true);
            return joinPoint.proceed();
        } finally {
            //System.out.println("切回user:" + joinPoint.getSignature().getName());
            TenantContext.get().setUseMasterDatabase(false);
        }
    }

}
