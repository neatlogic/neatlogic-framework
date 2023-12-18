package neatlogic.framework.util;

import neatlogic.framework.common.config.LocalConfig;
import neatlogic.framework.dto.TenantVo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class JdbcUtil {
    static Logger logger = LoggerFactory.getLogger(JdbcUtil.class);
    private static final String CONFIG_FILE = "config.properties";
    private Properties properties;

    private static final DriverManagerDataSource dataSource = new DriverManagerDataSource();

    static {
        dataSource.setDriverClassName(LocalConfig.dbConfigMap.get("db.driverClassName").toString());
        dataSource.setUrl(LocalConfig.dbConfigMap.get("db.url").toString());
        dataSource.setUsername(LocalConfig.dbConfigMap.get("db.username").toString());
        dataSource.setPassword(LocalConfig.dbConfigMap.get("db.password").toString());
    }

    public static Connection getNeatlogicConnection() {
        Connection connection;
        try {
            connection = dataSource.getConnection();
        } catch (Exception exception) {
            throw new RuntimeException("ERROR: " + I18nUtils.getStaticMessage("nfb.moduleinitializer.getactivetenantlist.neatlogicdb",LocalConfig.getPropertiesFrom()),exception);
        }
        return connection;
    }

    public static Connection getNeatlogicTenantConnection(TenantVo tenantVo, boolean isData) {
        Connection connection;
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(tenantVo.getDatasource().getDriver());
        String url = tenantVo.getDatasource().getUrl();
        url = url.replace("{host}", tenantVo.getDatasource().getHost());
        url = url.replace("{port}", tenantVo.getDatasource().getPort().toString());
        url = url.replace("{dbname}", "neatlogic_" + tenantVo.getUuid() + (isData ? "_data" : StringUtils.EMPTY));
        dataSource.setUrl(url);
        dataSource.setUsername(tenantVo.getDatasource().getUsername());
        dataSource.setPassword(tenantVo.getDatasource().getPasswordPlain());
        try {
            connection = dataSource.getConnection();
        } catch (Exception exception) {
            throw new RuntimeException("ERROR: " + I18nUtils.getStaticMessage("nfs.scriptrunnermanager.runscriptoncewithjdbc.tenantnotconnect", tenantVo.getUuid()));
        }
        return connection;
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