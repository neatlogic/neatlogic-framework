package neatlogic.framework.common.util;

import java.lang.reflect.InvocationTargetException;

/**
 * 
 * @Author:chenqiwei
 * @Time:Sep 28, 2018
 * @ClassName: TransactionDebugUtils
 * @Description: 用于检查事务是否启动
 */
public class TransactionDebugUtils {

	// Some guidance from:
	// http://java.dzone.com/articles/monitoring-declarative-transac?page=0,1
	public static boolean printTransactionInfo(String message) {
		try {
			ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
			Class tsmClass = contextClassLoader.loadClass("org.springframework.transaction.support.TransactionSynchronizationManager");
			Boolean isActive = (Boolean) tsmClass.getMethod("isActualTransactionActive", null).invoke(null, null);
			String transactionName = (String) tsmClass.getMethod("getCurrentTransactionName", null).invoke(null, null);
			if (isActive) {
				System.out.println("标记：" + message + "，事务名称：" + transactionName);
			} else {
				System.out.println("标记：" + message + "没有事务");
			}
			return isActive;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}

		// If we got here it means there was an exception
		throw new IllegalStateException("ServerUtils.transactionActive was unable to complete properly");
	}

}
