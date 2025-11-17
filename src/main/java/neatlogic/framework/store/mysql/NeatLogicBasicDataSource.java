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
import neatlogic.framework.common.util.RC4Util;
import neatlogic.framework.dao.plugin.ExecutingSQLInterceptor;
import neatlogic.framework.util.ThreadUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.CannotGetJdbcConnectionException;

import java.io.IOException;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLTransientConnectionException;
import java.sql.Statement;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class NeatLogicBasicDataSource extends HikariDataSource {//替换dbcp2的BasicDataSource
    private static final Logger logger = LoggerFactory.getLogger(NeatLogicBasicDataSource.class);

    // 保存上次查询ShowProcesslist命令的时间毫秒数
    private static volatile long lastShowProcesslistMilliseconds = -1;
    private static volatile int count = -1;

    /**
     * 五分钟内只打印三次日志
     */
    private synchronized void audit() {
        boolean flag = false;
        long currentTimeMillis = System.currentTimeMillis();
        long interval = currentTimeMillis - lastShowProcesslistMilliseconds;
        if (interval > TimeUnit.MINUTES.toMillis(5)) {
            count = 0;
            flag = true;
        } else {
            if (count < 3) {
                count++;
                flag = true;
            }
        }
        if (flag) {
            try {
                Map<Thread, String> thread2ExecutingSQLMap = ExecutingSQLInterceptor.getThread2ExecutingSQLMap();
                StringWriter writer = new StringWriter();
                ThreadUtil.dumpTraces(writer);
                writer.write("=================正在执行的SQL语句有" + thread2ExecutingSQLMap.size() + "条=================");
                for (Map.Entry<Thread, String> entry : thread2ExecutingSQLMap.entrySet()) {
                    Thread thread = entry.getKey();
                    String value = entry.getValue();
                    writer.write("[" + thread.getName() + "] 线程正在执行 " + value);
                    writer.write(System.lineSeparator());
                }
                Logger SQLTransientConnectionExceptionAuditLogger = LoggerFactory.getLogger("SQLTransientConnectionExceptionAudit");
                SQLTransientConnectionExceptionAuditLogger.error(writer.toString());
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        Connection conn = null;
        try {
            conn = super.getConnection();
        } catch (CannotGetJdbcConnectionException | SQLTransientConnectionException ex) {
            audit();
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
