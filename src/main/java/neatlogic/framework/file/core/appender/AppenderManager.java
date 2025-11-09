/*
 *
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the NeatLogic Sustainable Use License (NSUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */

package neatlogic.framework.file.core.appender;

import ch.qos.logback.core.util.FileSize;
import neatlogic.framework.asynchronization.taskmanager.AsyncTaskManager;
import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.common.config.Config;
import neatlogic.framework.file.core.Event;
import neatlogic.framework.file.core.IAuditType;
import neatlogic.framework.file.core.IEvent;
import neatlogic.framework.file.core.encoder.PatternLayoutEncoder;
import neatlogic.framework.file.core.rolling.FixedWindowRollingPolicy;
import neatlogic.framework.file.core.rolling.SizeBasedTriggeringPolicy;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class AppenderManager {

    private static ConcurrentMap<String, Appender<IEvent>> appenderCache = new ConcurrentHashMap<>();

    public static void execute(Event event) {
        AsyncTaskManager<Event> instance = AsyncTaskManager.getInstance(event.getAuditType().getType().toUpperCase() + "-THREAD", 1, event1 -> {
            Appender<IEvent> appender = AppenderManager.getAppender(event1.getAuditType());
            appender.doAppend(event1);
        });
        instance.submitTask(event);
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
            String path = Config.AUDIT_HOME() + TenantContext.get().getTenantUuid() + File.separator + auditType + File.separator + fileName;
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
