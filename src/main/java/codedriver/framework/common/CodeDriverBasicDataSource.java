/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.common;

import codedriver.framework.asynchronization.threadlocal.UserContext;
import codedriver.framework.common.util.RC4Util;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class CodeDriverBasicDataSource extends BasicDataSource {
    private final Logger logger = LoggerFactory.getLogger(CodeDriverBasicDataSource.class);

    @Override
    public Connection getConnection() throws SQLException {
        Connection conn = super.getConnection();
        Statement statement = conn.createStatement();
        //禁用mysql join顺序优化器,避免大SQL分析时间过慢
        statement.execute("SET SESSION optimizer_search_depth = 0");
        if (UserContext.get() != null) {
            String timezone = UserContext.get().getTimezone();
            if (StringUtils.isNotBlank(timezone)) {
                try {
                    statement.execute("SET time_zone = '" + timezone + "'");
                } catch (Exception ex) {
                    logger.error(ex.getMessage(), ex);
                } finally {
                    try {
                        statement.close();
                    } catch (SQLException ignored) {
                    }
                }
            }
        }
        return conn;
    }

    @Override
    public void setPassword(String password) {
        String prefix = "{ENCRYPTED}";
        if (password.startsWith(prefix)) {
            password = password.substring(prefix.length());
            password = RC4Util.decrypt(password);
        }
        super.setPassword(password);
    }
}
