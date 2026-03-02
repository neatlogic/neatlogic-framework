package neatlogic.framework.common.util;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @Author:chenqiwei
 * @Time:Sep 28, 2018
 * @ClassName: TransactionDebugUtils
 * @Description: 用于检查事务是否启动
 */
public class TransactionDebugUtils {
    private static final ThreadLocal<String> LAST_TX_FINGERPRINT = new ThreadLocal<>();
    private static final AtomicLong TX_SEQUENCE = new AtomicLong(0);

    // Some guidance from:
    // http://java.dzone.com/articles/monitoring-declarative-transac?page=0,1
    public static boolean printTransactionInfo(String message) {
        try {
            ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
            Class tsmClass = contextClassLoader.loadClass("org.springframework.transaction.support.TransactionSynchronizationManager");
            Boolean isActive = (Boolean) tsmClass.getMethod("isActualTransactionActive", null).invoke(null, null);
            String transactionName = (String) tsmClass.getMethod("getCurrentTransactionName", null).invoke(null, null);
            if (isActive) {
                Map resourceMap = (Map) tsmClass.getMethod("getResourceMap", null).invoke(null, null);
                String txFingerprint = buildTxFingerprint(transactionName, resourceMap);
                String lastFingerprint = LAST_TX_FINGERPRINT.get();
                boolean sameAsLast = txFingerprint.equals(lastFingerprint);
                long sequence = TX_SEQUENCE.incrementAndGet();
                LAST_TX_FINGERPRINT.set(txFingerprint);
                System.out.println("标记：" + message
                        + "，事务标识：" + transactionName
                        + "，事务指纹：" + txFingerprint
                        + "，与上一次调用是否同一事务：" + (sameAsLast ? "是" : "否")
                        + "，调用序号：" + sequence);
            } else {
                LAST_TX_FINGERPRINT.remove();
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

    private static String buildTxFingerprint(String transactionName, Map resourceMap) {
        StringBuilder keyBuilder = new StringBuilder();
        keyBuilder.append(transactionName == null ? "<unnamed>" : transactionName).append('|');
        if (resourceMap == null || resourceMap.isEmpty()) {
            keyBuilder.append("no-resource");
            return keyBuilder.toString();
        }
        List<Object> keys = new ArrayList<>(resourceMap.keySet());
        Collections.sort(keys, (a, b) -> Integer.compare(System.identityHashCode(a), System.identityHashCode(b)));
        for (Object key : keys) {
            Object value = resourceMap.get(key);
            keyBuilder.append(System.identityHashCode(key))
                    .append(':')
                    .append(value == null ? "null" : System.identityHashCode(value))
                    .append(';');
        }
        return keyBuilder.toString();
    }

}
