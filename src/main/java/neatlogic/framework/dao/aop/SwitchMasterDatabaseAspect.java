package neatlogic.framework.dao.aop;

import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.common.RootComponent;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Aspect
@RootComponent
public class SwitchMasterDatabaseAspect {

    private final DataSourceTransactionManager transactionManager;

    @Autowired
    public SwitchMasterDatabaseAspect(DataSourceTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    @Around("@annotation(neatlogic.framework.dao.aop.UseMasterDatabase) || @within(neatlogic.framework.dao.aop.UseMasterDatabase)")
    public Object switchMasterDatabase(ProceedingJoinPoint joinPoint) throws Throwable {
        if (!TransactionSynchronizationManager.isActualTransactionActive()) {
            return proceedWithMasterDatabase(joinPoint);
        }

        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        // 使用 NOT_SUPPORTED：挂起当前事务，并以“非事务”方式执行当前调用。
        // 这样可以避免复用外层事务已绑定的租户连接，从而保证切库生效。
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_NOT_SUPPORTED);
        TransactionStatus status = transactionManager.getTransaction(def);

        try {
            Object result = proceedWithMasterDatabase(joinPoint);
            // 在 NOT_SUPPORTED 下，这里的 commit() 只是做状态收尾与恢复外层事务上下文，不是数据库物理提交。
            transactionManager.commit(status);
            return result;
        } catch (Throwable ex) {
            // NOT_SUPPORTED 的异常收尾路径；这里不会回滚业务事务（因为当前段本身非事务执行）。
            transactionManager.rollback(status);
            throw ex;
        }
    }

    private Object proceedWithMasterDatabase(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            TenantContext.get().setUseMasterDatabase(true);
            return joinPoint.proceed();
        } finally {
            TenantContext.get().setUseMasterDatabase(false);
        }
    }
}
