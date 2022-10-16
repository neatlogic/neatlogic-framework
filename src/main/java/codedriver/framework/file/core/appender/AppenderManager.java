/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.file.core.appender;

import ch.qos.logback.core.util.FileSize;
import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.common.config.Config;
import codedriver.framework.file.core.IAuditType;
import codedriver.framework.file.core.IEvent;
import codedriver.framework.file.core.encoder.PatternLayoutEncoder;
import codedriver.framework.file.core.rolling.FixedWindowRollingPolicy;
import codedriver.framework.file.core.rolling.SizeBasedTriggeringPolicy;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class AppenderManager {
    private static ConcurrentMap<String, Appender<IEvent>> appenderCache = new ConcurrentHashMap<>();

    public static Appender<IEvent> getAppender(IAuditType auditType) {
        String tenantUuid = TenantContext.get().getTenantUuid();
        String id = tenantUuid + "_" + auditType.getType();
        Appender<IEvent> appender = appenderCache.get(id);
        if (appender == null) {
            synchronized (AppenderManager.class) {
                appender = appenderCache.get(id);
                if (appender == null) {
                    appender = new AppenderManager.Builder(auditType.getType(), auditType.getFileName(), auditType.getMessagePattern())
                            .withAsync(auditType.getAsync())
                            .withDiscardingThreshold(auditType.getDiscardingThreshold())
                            .withQueueSize(auditType.getQueueSize())
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
        private boolean async;
        private int discardingThreshold;
        private int queueSize;
        private String fileName;
        private String maxFileSize;
        private String messagePattern;

        public Builder(String auditType, String fileName, String messagePattern) {
            this.auditType = auditType;
            this.fileName = fileName;
            this.messagePattern = messagePattern;
        }

        public Builder withAsync(boolean async) {
            this.async = async;
            return this;
        }

        public Builder withDiscardingThreshold(int discardingThreshold) {
            this.discardingThreshold = discardingThreshold;
            return this;
        }

        public Builder withQueueSize(int queueSize) {
            this.queueSize = queueSize;
            return this;
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

            if (async) {
                AsyncAppender asyncAppender = new AsyncAppender();
                asyncAppender.setDiscardingThreshold(discardingThreshold);
                asyncAppender.setQueueSize(queueSize);
                asyncAppender.setAppender(rollingFileAppender);
                asyncAppender.start();
                return asyncAppender;
            } else {
                return rollingFileAppender;
            }
        }
    }
}
