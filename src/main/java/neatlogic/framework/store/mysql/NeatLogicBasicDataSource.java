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

import com.zaxxer.hikari.HikariDataSource;
import neatlogic.framework.asynchronization.threadlocal.UserContext;
import neatlogic.framework.common.config.Config;
import neatlogic.framework.common.util.RC4Util;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.CannotGetJdbcConnectionException;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLTransientConnectionException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class NeatLogicBasicDataSource extends HikariDataSource {//替换dbcp2的BasicDataSource
    private static final Logger logger = LoggerFactory.getLogger(NeatLogicBasicDataSource.class);

    // 保存上次查询ShowProcesslist命令的时间毫秒数
    private static volatile long lastShowProcesslistMilliseconds = -1;
    // 持有数据库连接的线程Map
    private final static Map<Connection, Thread> holdingConnectionThreadMap = new ConcurrentHashMap<>();

    public static void addHoldingConnectionThreadByConnection(Connection connection) {
        int size = holdingConnectionThreadMap.size();
        if (size <= Config.DATASOURCE_MAXIMUN_POOL_SIZE()) {
            holdingConnectionThreadMap.put(connection, Thread.currentThread());
        } else {
            logger.error("持有数据库连接的线程Map大小为{}, 数据库连接池最大数量为{}", size, Config.DATASOURCE_MAXIMUN_POOL_SIZE());
        }
    }

    public static Thread removeHoldingConnectionThreadByConnection(Connection connection) {
        return holdingConnectionThreadMap.remove(connection);
    }

    public static String getHoldingConnectionThreadStackTrace(Map<Connection, Thread> holdingConnectionThreadMap) {
        ThreadMXBean mxBean = ManagementFactory.getThreadMXBean();
        ThreadInfo[] threadInfos = mxBean.getThreadInfo(mxBean.getAllThreadIds(), 0);
        Map<Long, ThreadInfo> threadInfoMap = new HashMap<>();
        for (ThreadInfo threadInfo : threadInfos) {
            threadInfoMap.put(threadInfo.getThreadId(), threadInfo);
        }
        StringBuilder stringBuilder = new StringBuilder(System.lineSeparator());
        stringBuilder.append("总线程数为: ").append(holdingConnectionThreadMap.size()).append(System.lineSeparator());
        for (Map.Entry<Connection, Thread> entry : holdingConnectionThreadMap.entrySet()) {
            Thread thread = entry.getValue();
            stringBuilder.append("[").append(thread.getName()).append("] prio=").append(thread.getPriority())
                    .append(" tid=").append(thread.getId())
                    .append(" ").append(thread.getState())
                    .append(" ").append(thread.isDaemon() ? "deamon" : "worker");
            ThreadInfo threadInfo = threadInfoMap.get(thread.getId());
            if (threadInfo != null) {
                stringBuilder.append(" native=").append(threadInfo.isInNative())
                        .append(", suspended=").append(threadInfo.isSuspended())
                        .append(", block=").append(threadInfo.getBlockedCount())
                        .append(", wait=").append(threadInfo.getWaitedCount())
                        .append(" lock=").append(threadInfo.getLockName())
                        .append(" owned by ").append(threadInfo.getLockOwnerName())
                        .append(" (").append(threadInfo.getLockOwnerId())
                        .append("), cpu=").append(mxBean.getThreadCpuTime(threadInfo.getThreadId()) / 1000000L)
                        .append(", user=").append(mxBean.getThreadUserTime(threadInfo.getThreadId()) / 1000000L);
            }
            stringBuilder.append(System.lineSeparator());
            StackTraceElement[] stackTrace = thread.getStackTrace();
            for (StackTraceElement stackTraceElement : stackTrace) {
                stringBuilder.append("    at ").append(stackTraceElement).append(System.lineSeparator());
            }
        }
        return stringBuilder.toString();
    }

    private synchronized void audit(Map<Connection, Thread> holdingConnectionThreadMap) {
        long currentTimeMillis = System.currentTimeMillis();
        long interval = currentTimeMillis - lastShowProcesslistMilliseconds;
        if (interval > TimeUnit.MINUTES.toMillis(1)) {
            lastShowProcesslistMilliseconds = currentTimeMillis;
            Logger SQLTransientConnectionExceptionAuditLogger = LoggerFactory.getLogger("SQLTransientConnectionExceptionAudit");
            SQLTransientConnectionExceptionAuditLogger.error(getHoldingConnectionThreadStackTrace(holdingConnectionThreadMap));
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        Connection conn = null;
        try {
            conn = super.getConnection();
            if (Config.DATASOURCE_CONNECTION_HOLDER_TRACK_ENABLE()) {
                addHoldingConnectionThreadByConnection(conn);
                conn = new NeatLogicConnection(conn);
            }
        } catch (CannotGetJdbcConnectionException | SQLTransientConnectionException ex) {
            if (Config.DATASOURCE_CONNECTION_HOLDER_TRACK_ENABLE()) {
                audit(new HashMap<>(holdingConnectionThreadMap));
            }
            throw ex;
        }
        conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
        try (Statement statement = conn.createStatement()) {
            if (Objects.equals(DatasourceManager.getDatabaseId(), DatabaseVendor.MYSQL.getDatabaseId())) {
                //设置mysql join顺序优化器最大深度是5,避免大SQL分析时间过慢
                statement.execute("SET SESSION optimizer_search_depth = 5");
                //设置join_buffer为16M，提升BNL性能
                statement.execute("SET SESSION join_buffer_size = 16777216");
            }
            if (UserContext.get() != null) {
                String timezone = UserContext.get().getTimezone();
                if (StringUtils.isNotBlank(timezone)) {
                    statement.execute("SET time_zone = '" + timezone + "'");
                }
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
        return conn;
    }

    @Override
    public void setPassword(String password) {
        password = RC4Util.decrypt(password);
        super.setPassword(password);
    }
}
