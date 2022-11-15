package codedriver.framework.transaction.util;

import codedriver.framework.common.RootComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

@RootComponent
public class TransactionUtil {
    private static DataSourceTransactionManager dataSourceTransactionManager;

    @Autowired
    public TransactionUtil(DataSourceTransactionManager _dataSourceTransactionManager) {
        dataSourceTransactionManager = _dataSourceTransactionManager;
    }


    // 开启事务
    public static TransactionStatus openTx() {
        return openTxWithPropagationBehavior(null);
    }

    public static TransactionStatus openNewTx() {
        return openTxWithPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
    }

    /**
     * 使用指定的传播行为开启事务，如果不指定，则默认使用PROPAGATION_REQUIRED
     *
     * @param propagationBehavior TransactionDefinition中定义的传播行为
     * @return TransactionStatus
     */
    private static TransactionStatus openTxWithPropagationBehavior(Integer propagationBehavior) {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        def.setPropagationBehavior(propagationBehavior != null ? propagationBehavior : TransactionDefinition.PROPAGATION_REQUIRED);
        return dataSourceTransactionManager.getTransaction(def);
    }

    // 提交事务
    public static void commitTx(TransactionStatus ts) {
        dataSourceTransactionManager.commit(ts);
    }

    // 回滚事务
    public static void rollbackTx(TransactionStatus ts) {
        dataSourceTransactionManager.rollback(ts);
    }

}
