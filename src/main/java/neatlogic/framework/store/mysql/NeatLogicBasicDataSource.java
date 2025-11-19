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
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.CannotGetJdbcConnectionException;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLTransientConnectionException;
import java.sql.Statement;
import java.util.Objects;

public class NeatLogicBasicDataSource extends HikariDataSource {//替换dbcp2的BasicDataSource
    private final Logger logger = LoggerFactory.getLogger(NeatLogicBasicDataSource.class);

    @Override
    public Connection getConnection() throws SQLException {
        Connection conn = null;
        try {
            conn = super.getConnection();
        } catch (CannotGetJdbcConnectionException | SQLTransientConnectionException ex) {
            SQLTransientConnectionExceptionAudit.audit();
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
