/*
Copyright(c) $today.year NeatLogic Co., Ltd. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package neatlogic.framework.file.core.appender;

import ch.qos.logback.core.util.FileSize;
import neatlogic.framework.asynchronization.thread.NeatLogicThread;
import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.common.config.Config;
import neatlogic.framework.file.core.IAuditType;
import neatlogic.framework.file.core.IEvent;
import neatlogic.framework.file.core.encoder.PatternLayoutEncoder;
import neatlogic.framework.file.core.rolling.FixedWindowRollingPolicy;
import neatlogic.framework.file.core.rolling.SizeBasedTriggeringPolicy;
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

    public static void execute(NeatLogicThread command) {
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
                    appender = new AppenderManager.Builder(auditType.getType(), auditType.getFileName())
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

        public Builder(String auditType, String fileName) {
            this.auditType = auditType;
            this.fileName = fileName;
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
            fixedWindowRollingPolicy.setMinIndex(1);
            fixedWindowRollingPolicy.setMaxIndex(100);
            fixedWindowRollingPolicy.setParent(rollingFileAppender);
            rollingFileAppender.setRollingPolicy(fixedWindowRollingPolicy);

            SizeBasedTriggeringPolicy sizeBasedTriggeringPolicy = new SizeBasedTriggeringPolicy();
            sizeBasedTriggeringPolicy.setMaxFileSize(FileSize.valueOf(maxFileSize));
            rollingFileAppender.setTriggeringPolicy(sizeBasedTriggeringPolicy);

            PatternLayoutEncoder encoder = new PatternLayoutEncoder();
            encoder.start();
            rollingFileAppender.setEncoder(encoder);

            sizeBasedTriggeringPolicy.start();
            fixedWindowRollingPolicy.start();
            rollingFileAppender.start();
            return rollingFileAppender;
        }
    }
}
