package neatlogic.framework.common.util;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @Author:chenqiwei
 * @Time:Sep 28, 2018
 * @ClassName: TransactionDebugUtils
 * @Description: 用于检查事务是否启动
 */
public class TransactionDebugUtils {
    private static final ThreadLocal<String> LAST_TX_FINGERPRINT = new ThreadLocal<>();
    private static final ThreadLocal<Map<String, String>> TX_ORIGIN_MAP = ThreadLocal.withInitial(HashMap::new);
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
                String txOrigin = TX_ORIGIN_MAP.get().computeIfAbsent(txFingerprint, key -> detectTxOrigin(transactionName));
                System.out.println("标记：" + message
                        + "，事务状态：有事务"
                        + "，事务标识：" + transactionName
                        + "，事务指纹：" + txFingerprint
                        + "，事务源头：" + txOrigin
                        + "，与上一次调用是否同一事务：" + (sameAsLast ? "是" : "否")
                        + "，调用序号：" + sequence);
            } else {
                LAST_TX_FINGERPRINT.remove();
                TX_ORIGIN_MAP.remove();
                System.out.println("标记：" + message + "，事务状态：没有事务");
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

    /**
     * 记录当前线程事务的创建源头（如果尚未记录）
     */
    public static void recordTransactionOrigin(String hint) {
        try {
            ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
            Class tsmClass = contextClassLoader.loadClass("org.springframework.transaction.support.TransactionSynchronizationManager");
            Boolean isActive = (Boolean) tsmClass.getMethod("isActualTransactionActive", null).invoke(null, null);
            if (!Boolean.TRUE.equals(isActive)) {
                return;
            }
            String transactionName = (String) tsmClass.getMethod("getCurrentTransactionName", null).invoke(null, null);
            Map resourceMap = (Map) tsmClass.getMethod("getResourceMap", null).invoke(null, null);
            String txFingerprint = buildTxFingerprint(transactionName, resourceMap);
            TX_ORIGIN_MAP.get().putIfAbsent(txFingerprint, buildOrigin(hint));
        } catch (Exception ignored) {
            // 调试工具不影响业务流程
        }
    }

    private static String buildOrigin(String hint) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (StackTraceElement ste : stackTrace) {
            String className = ste.getClassName();
            if (className.equals(TransactionDebugUtils.class.getName())
                    || className.equals(Thread.class.getName())
                    || className.startsWith("java.lang.reflect.")
                    || className.startsWith("sun.reflect.")
                    || className.startsWith("jdk.internal.reflect.")) {
                continue;
            }
            return hint + "@" + ste.getClassName() + "." + ste.getMethodName() + ":" + ste.getLineNumber();
        }
        return hint;
    }

    private static String detectTxOrigin(String transactionName) {
        if (transactionName != null && !transactionName.trim().isEmpty()) {
            return "Spring事务名：" + transactionName;
        }
        return "首次观测调用栈：" + buildBusinessStack();
    }

    private static String buildBusinessStack() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        StringBuilder builder = new StringBuilder();
        int count = 0;
        for (StackTraceElement ste : stackTrace) {
            String className = ste.getClassName();
            if (className.equals(Thread.class.getName())
                    || className.equals(TransactionDebugUtils.class.getName())
                    || className.startsWith("java.lang.reflect.")
                    || className.startsWith("sun.reflect.")
                    || className.startsWith("jdk.internal.reflect.")
                    || className.startsWith("org.springframework.")
                    || className.startsWith("org.apache.ibatis.")
                    || className.startsWith("com.sun.proxy.")
                    || className.startsWith("jdk.proxy.")) {
                continue;
            }
            if (count > 0) {
                builder.append(" <- ");
            }
            builder.append(className).append('.').append(ste.getMethodName()).append(':').append(ste.getLineNumber());
            count++;
            if (count >= 6) {
                break;
            }
        }
        if (count == 0) {
            return "未知（调用栈已被代理层吞噬）";
        }
        return builder.toString();
    }

}
