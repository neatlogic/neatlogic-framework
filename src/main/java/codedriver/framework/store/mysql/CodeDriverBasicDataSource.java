/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.store.mysql;

import codedriver.framework.asynchronization.threadlocal.UserContext;
import codedriver.framework.common.util.RC4Util;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class CodeDriverBasicDataSource extends HikariDataSource {//替换dbcp2的BasicDataSource
    private final Logger logger = LoggerFactory.getLogger(CodeDriverBasicDataSource.class);

    @Override
    public Connection getConnection() throws SQLException {
        Connection conn = super.getConnection();
        conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
        try (Statement statement = conn.createStatement()) {
            //设置mysql join顺序优化器最大深度是5,避免大SQL分析时间过慢
            statement.execute("SET SESSION optimizer_search_depth = 5");
            //设置join_buffer为16M，提升BNL性能
            statement.execute("SET SESSION join_buffer_size = 16777216");
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
