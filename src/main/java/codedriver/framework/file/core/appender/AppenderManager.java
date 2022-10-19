/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.file.core.appender;

import ch.qos.logback.core.util.FileSize;
import codedriver.framework.asynchronization.thread.CodeDriverThread;
import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.common.config.Config;
import codedriver.framework.file.core.IAuditType;
import codedriver.framework.file.core.IEvent;
import codedriver.framework.file.core.encoder.PatternLayoutEncoder;
import codedriver.framework.file.core.rolling.FixedWindowRollingPolicy;
import codedriver.framework.file.core.rolling.SizeBasedTriggeringPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.concurrent.*;

public class AppenderManager {

    private static final Logger logger = LoggerFactory.getLogger(AppenderManager.class);

    private static final ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
            1, 3,
            60L, TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(1000), new ThreadPoolExecutor.DiscardPolicy());
    static {
        // 如果阻塞队列中没有任务，线程池中活跃线程数降为0
        threadPool.allowCoreThreadTimeOut(true);
    }
    private static ConcurrentMap<String, Appender<IEvent>> appenderCache = new ConcurrentHashMap<>();

    public static void execute(CodeDriverThread command) {
        try {
            threadPool.execute(command);
        } catch (RejectedExecutionException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public static Appender<IEvent> getAppender(IAuditType auditType) {
        String tenantUuid = TenantContext.get().getTenantUuid();
        String id = tenantUuid + "_" + auditType.getType();
        Appender<IEvent> appender = appenderCache.get(id);
        if (appender == null) {
            synchronized (AppenderManager.class) {
                appender = appenderCache.get(id);
                if (appender == null) {
                    appender = new AppenderManager.Builder(auditType.getType(), auditType.getFileName(), auditType.getMessagePattern())
                            .withMaxFileSize(auditType.getMaxFileSize())
                            .build();
                    appenderCache.put(id, appender);
                }
            }
        }
        return appender;
    }

    private static class Builder {
        private String auditType;
        private String fileName;
        private String maxFileSize;
        private String messagePattern;

        public Builder(String auditType, String fileName, String messagePattern) {
            this.auditType = auditType;
            this.fileName = fileName;
            this.messagePattern = messagePattern;
        }

        public Builder withMaxFileSize(String maxFileSize) {
            this.maxFileSize = maxFileSize;
            return this;
        }

        public Appender<IEvent> build() {
            String path = Config.DATA_HOME() + TenantContext.get().getTenantUuid() + File.separator + auditType + File.separator + fileName;
            RollingFileAppender<IEvent> rollingFileAppender = new RollingFileAppender<>();
            rollingFileAppender.setFile(path);
            FixedWindowRollingPolicy fixedWindowRollingPolicy = new FixedWindowRollingPolicy();
            fixedWindowRollingPolicy.setFileNamePattern(path + ".%i");
            fixedWindowRollingPolicy.setMinIndex(1);
            fixedWindowRollingPolicy.setMaxIndex(100);
            fixedWindowRollingPolicy.setParent(rollingFileAppender);
            rollingFileAppender.setRollingPolicy(fixedWindowRollingPolicy);

            SizeBasedTriggeringPolicy sizeBasedTriggeringPolicy = new SizeBasedTriggeringPolicy();
            sizeBasedTriggeringPolicy.setMaxFileSize(FileSize.valueOf(maxFileSize));
            rollingFileAppender.setTriggeringPolicy(sizeBasedTriggeringPolicy);

            PatternLayoutEncoder encoder = new PatternLayoutEncoder();
            encoder.setPattern(messagePattern);
            encoder.start();
            rollingFileAppender.setEncoder(encoder);

            sizeBasedTriggeringPolicy.start();
            fixedWindowRollingPolicy.start();
            rollingFileAppender.start();
            return rollingFileAppender;
        }
    }
}
