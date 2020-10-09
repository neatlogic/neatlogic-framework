package codedriver.framework.common;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import codedriver.framework.common.util.RC4Util;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import codedriver.framework.asynchronization.threadlocal.UserContext;

public class CodeDriverBasicDataSource extends BasicDataSource {
    private Logger logger = LoggerFactory.getLogger(CodeDriverBasicDataSource.class);

    @Override
    public Connection getConnection() throws SQLException {
        Connection conn = super.getConnection();
        Statement statement = null;
        if (UserContext.get() != null) {
        	String timezone = UserContext.get().getTimezone();
        	if (StringUtils.isNotBlank(timezone)) {
        		try {
        			statement = conn.createStatement();
        			statement.execute("SET time_zone = \'" + timezone + "\'");
        		} catch (Exception ex) {
        			logger.error(ex.getMessage(), ex);
        		} finally {
        			try {
        				if (statement != null) {
        					statement.close();
        				}
        			} catch (SQLException e) {
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
