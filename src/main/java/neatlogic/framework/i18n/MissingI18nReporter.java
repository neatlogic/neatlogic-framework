package neatlogic.framework.i18n;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 按 JVM 参数控制缺失翻译诊断，并执行有界去重和安全日志输出。
 * 默认关闭，使用 {@code -DenableI18nExceptionLog=true} 开启。
 */
final class MissingI18nReporter {
    static final String ENABLE_PROPERTY = "enableI18nExceptionLog";
    private static final Logger logger = LoggerFactory.getLogger("i18nException");
    private static final int DEFAULT_MAXIMUM_SIZE = 4096;
    private static final int MAXIMUM_KEY_LENGTH = 512;

    private final boolean enabled;
    private final int maximumSize;
    private final Set<String> reportedKeys = new HashSet<>();
    private final AtomicLong suppressedCount = new AtomicLong();

    /** 按 JVM 参数决定是否启用，并使用默认容量创建诊断器。 */
    MissingI18nReporter() {
        this(Boolean.parseBoolean(System.getProperty(ENABLE_PROPERTY, "false")), DEFAULT_MAXIMUM_SIZE);
    }

    /** 使用指定开关和容量创建诊断器，供隔离测试验证边界。 */
    MissingI18nReporter(boolean enabled, int maximumSize) {
        if (maximumSize <= 0) {
            throw new IllegalArgumentException("maximumSize must be greater than zero");
        }
        this.enabled = enabled;
        this.maximumSize = maximumSize;
    }

    /** 记录缺失 key；同一安全化 key 仅输出一次，超过容量后只输出指数级汇总。 */
    void report(String language, String key) {
        // 迁移期默认关闭诊断，必须在任何字符串处理、缓存和同步操作之前返回。
        if (!enabled) {
            return;
        }
        String safeKey = sanitize(key);
        boolean shouldLog;
        synchronized (reportedKeys) {
            String identity = language + ':' + safeKey;
            if (reportedKeys.contains(identity)) {
                return;
            }
            shouldLog = reportedKeys.size() < maximumSize;
            if (shouldLog) {
                reportedKeys.add(identity);
            }
        }
        if (shouldLog) {
            logger.error("语言资源缺少翻译，language: {}, key: {}", language, safeKey);
            return;
        }
        long count = suppressedCount.incrementAndGet();
        if ((count & (count - 1)) == 0) {
            logger.error("语言资源缺失诊断已达到容量上限，maximumSize: {}, suppressedCount: {}", maximumSize, count);
        }
    }

    /** 返回当前去重缓存大小。 */
    int size() {
        if (!enabled) {
            return 0;
        }
        synchronized (reportedKeys) {
            return reportedKeys.size();
        }
    }

    /** 返回超过容量后被抑制的事件数量。 */
    long getSuppressedCount() {
        return suppressedCount.get();
    }

    /** 清理控制字符并限制日志与缓存中的 key 长度。 */
    private String sanitize(String key) {
        String source = String.valueOf(key);
        StringBuilder result = new StringBuilder(Math.min(source.length(), MAXIMUM_KEY_LENGTH));
        for (int index = 0; index < source.length() && result.length() < MAXIMUM_KEY_LENGTH; index++) {
            char character = source.charAt(index);
            result.append(Character.isISOControl(character) ? ' ' : character);
        }
        return result.toString();
    }
}
