package neatlogic.framework.util;

import neatlogic.framework.common.config.LocalConfig;
import neatlogic.framework.dto.TenantVo;
import neatlogic.framework.exception.module.ModuleInitRuntimeException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Properties;

public class JdbcUtil {
    static Logger logger = LoggerFactory.getLogger(JdbcUtil.class);

    public static Connection getNeatlogicConnection() {
        try {
            String url = LocalConfig.dbConfigMap.get("db.url").toString();
            String userName = LocalConfig.dbConfigMap.get("db.username").toString();
            String password = LocalConfig.dbConfigMap.get("db.password").toString();
            Class<?> clazz = Class.forName(LocalConfig.dbConfigMap.get("db.driverClassName").toString());
            Driver driver = ((Driver) clazz.newInstance());
            Properties props = new Properties();
            props.setProperty("user", userName);
            props.setProperty("password", password);
            // 设置自动重连
            props.setProperty("autoReconnect", "true");
            // 设置连接超时时间（单位：毫秒）
            props.setProperty("connectTimeout", "5000");
            // 设置socket超时时间
            props.setProperty("socketTimeout", "10000");
            // 设置最大允许的数据包大小
            props.setProperty("maxAllowedPacket", "67108864"); // 4 MB
            // 设置字符集
            //props.setProperty("useUnicode", "true");
            props.setProperty("characterEncoding", "UTF-8");
            // 使用SSL（根据数据库服务器配置决定是否启用）
            props.setProperty("useSSL", "false");
            // 设置JDBC驱动程序应该抛出异常而不是警告
            props.setProperty("jdbcCompliantTruncation", "true");
            return driver.connect(url, props);
        } catch (Throwable exception) {
            logger.error(exception.getMessage(), exception);
            throw new ModuleInitRuntimeException("ERROR: " + I18nUtils.getStaticMessage("nfb.moduleinitializer.getactivetenantlist.neatlogicdb", LocalConfig.getPropertiesFrom()), exception);
        }
    }

    public static Connection getNeatlogicTenantConnection(TenantVo tenantVo, boolean isData) {
        try {
            String url = tenantVo.getDatasource().getUrl();
            String userName = tenantVo.getDatasource().getUsername();
            String host = tenantVo.getDatasource().getHost();
            Integer port = tenantVo.getDatasource().getPort();
            url = url.replace("{host}", host);
            url = url.replace("{port}", port.toString());
            url = url.replace("{dbname}", "neatlogic_" + tenantVo.getUuid() + (isData ? "_data" : StringUtils.EMPTY));
            String password = tenantVo.getDatasource().getPasswordPlain();
            Class<?> clazz = Class.forName(tenantVo.getDatasource().getDriver());
            Driver driver = ((Driver) clazz.newInstance());
            Properties props = new Properties();
            props.setProperty("user", userName);
            props.setProperty("password", password);
            // 设置自动重连
            props.setProperty("autoReconnect", "true");
            // 设置连接超时时间（单位：毫秒）
            props.setProperty("connectTimeout", "5000");
            // 设置socket超时时间
            props.setProperty("socketTimeout", "10000");
            // 设置最大允许的数据包大小
            props.setProperty("maxAllowedPacket", "67108864"); // 4 MB
            // 设置字符集
            //props.setProperty("useUnicode", "true");
            props.setProperty("characterEncoding", "UTF-8");
            // 使用SSL（根据数据库服务器配置决定是否启用）
            props.setProperty("useSSL", "false");
            // 设置JDBC驱动程序应该抛出异常而不是警告
            props.setProperty("jdbcCompliantTruncation", "true");
            return driver.connect(url, props);
        } catch (Throwable exception) {
            logger.error(exception.getMessage(), exception);
            throw new ModuleInitRuntimeException("ERROR: " + I18nUtils.getStaticMessage("nfs.scriptrunnermanager.runscriptoncewithjdbc.tenantnotconnect", tenantVo.getUuid()));
        }
    }

    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void closeStatement(Statement statement) {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void closeResultSet(ResultSet resultSet) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}