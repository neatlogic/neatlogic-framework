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

package neatlogic.framework.store.mysql;

import com.alibaba.fastjson.JSON;
import com.zaxxer.hikari.HikariPoolMXBean;
import neatlogic.framework.common.config.Config;
import neatlogic.framework.dto.healthcheck.DataSourceInfoVo;
import neatlogic.framework.util.ThreadUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class SQLTransientConnectionExceptionAudit {

    private static final Logger logger = LoggerFactory.getLogger(SQLTransientConnectionExceptionAudit.class);

    // 保存上次抛异常的时间毫秒数
    private final static AtomicLong lastThrowExceptionMillisecondsAtomicLong = new AtomicLong(0);
    private final static AtomicInteger countAtomicInteger = new AtomicInteger(3);
    private final static Map<String, String> executingSQLMap = new ConcurrentHashMap<>();

    public static Map<String, String> getExecutingSQL() {
        return new HashMap<>(executingSQLMap);
    }

    public static void clearExecutingSQL() {
        executingSQLMap.clear();
    }

    public static void putExecutingSQL(String key, String value) {
        executingSQLMap.put(key, value);
    }

    public static void removeExecutingSQL(String key) {
        executingSQLMap.remove(key);
    }

    /**
     * 五分钟内只打印三次日志
     */
    public static void audit() {
        if (Objects.equals(Config.DATASOURCE_EXCEPTION_AUDIT(), 1)) {
            boolean flag = false;
            long currentTimeMillis = System.currentTimeMillis();
            long lastThrowExceptionMilliseconds = lastThrowExceptionMillisecondsAtomicLong.getAndUpdate(operand -> currentTimeMillis);
            long interval = currentTimeMillis - lastThrowExceptionMilliseconds;
            if (interval > TimeUnit.MINUTES.toMillis(5)) {
                if (countAtomicInteger.compareAndSet(3, 0)) {
                    flag = true;
                }
            } else {
                int count = countAtomicInteger.updateAndGet(operand -> {
                    if (operand < 3) {
                        return operand + 1;
                    } else {
                        return operand;
                    }
                });
                if (count < 3) {
                    flag = true;
                }
            }
            if (flag) {
                doAudit();
            }
        }
    }

    private static synchronized void doAudit() {
        try {
            DataSourceInfoVo dataSourceInfoVo = new DataSourceInfoVo();
            NeatLogicBasicDataSource datasource = DatasourceManager.getDatasource();
            dataSourceInfoVo.setPoolName(datasource.getPoolName());
            HikariPoolMXBean hikariPoolMXBean = datasource.getHikariPoolMXBean();
            if (hikariPoolMXBean != null) {
                dataSourceInfoVo.setIdleConnections(hikariPoolMXBean.getIdleConnections());
                dataSourceInfoVo.setActiveConnections(hikariPoolMXBean.getActiveConnections());
                dataSourceInfoVo.setThreadsAwaitingConnection(hikariPoolMXBean.getThreadsAwaitingConnection());
                dataSourceInfoVo.setTotalConnections(hikariPoolMXBean.getTotalConnections());
            }
            Map<String, String> executingSQLSnapshotMap = new HashMap<>(executingSQLMap);
            StringWriter writer = new StringWriter();
            ThreadUtil.dumpTraces(writer);
            writer.write("=================正在执行的SQL语句有" + executingSQLSnapshotMap.size() + "条=================");
            writer.write(System.lineSeparator());
            for (Map.Entry<String, String> entry : executingSQLSnapshotMap.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                writer.write("[" + key + "] 线程正在执行 " + value);
                writer.write(System.lineSeparator());
            }
            writer.write("连接池信息: " + JSON.toJSONString(dataSourceInfoVo));
            Logger exceptionAuditLogger = LoggerFactory.getLogger("exceptionAudit");
            exceptionAuditLogger.error(writer.toString());
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }
}
