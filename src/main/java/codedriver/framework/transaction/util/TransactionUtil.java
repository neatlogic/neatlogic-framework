package codedriver.framework.transaction.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import codedriver.framework.common.RootComponent;

@RootComponent
public class TransactionUtil {
	@Autowired
	private DataSourceTransactionManager dataSourceTransactionManager;

	// 开启事务
	public TransactionStatus openTx() {
		DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
		return dataSourceTransactionManager.getTransaction(def);
	}

	// 提交事务
	public void commitTx(TransactionStatus ts) {
		dataSourceTransactionManager.commit(ts);
	}

	// 回滚事务
	public void rollbackTx(TransactionStatus ts) {
		dataSourceTransactionManager.rollback(ts);
	}

}
