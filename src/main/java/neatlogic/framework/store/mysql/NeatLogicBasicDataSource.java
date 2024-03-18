/*Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.*/

package neatlogic.framework.store.mysql;

import com.zaxxer.hikari.HikariDataSource;
import neatlogic.framework.asynchronization.threadlocal.UserContext;
import neatlogic.framework.common.util.RC4Util;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;

public class NeatLogicBasicDataSource extends HikariDataSource {//替换dbcp2的BasicDataSource
    private final Logger logger = LoggerFactory.getLogger(NeatLogicBasicDataSource.class);

    @Override
    public Connection getConnection() throws SQLException {
        Connection conn = super.getConnection();
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
