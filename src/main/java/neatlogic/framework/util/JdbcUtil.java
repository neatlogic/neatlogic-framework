package neatlogic.framework.util;

import neatlogic.framework.common.config.LocalConfig;
import neatlogic.framework.dto.TenantVo;
import neatlogic.framework.exception.module.ModuleInitRuntimeException;
import neatlogic.framework.store.mysql.NeatLogicBasicDataSource;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class JdbcUtil {
    static Logger logger = LoggerFactory.getLogger(JdbcUtil.class);
    private static final Map<Object, Object> datasourceMap = new HashMap<>();
    private Properties properties;

    private static final NeatLogicBasicDataSource neatlogicDatasource = new NeatLogicBasicDataSource();

    static {

        String url = LocalConfig.dbConfigMap.get("db.url").toString();
        neatlogicDatasource.setJdbcUrl(url);
        neatlogicDatasource.setDriverClassName(LocalConfig.dbConfigMap.get("db.driverClassName").toString());
        neatlogicDatasource.setUsername(LocalConfig.dbConfigMap.get("db.username").toString());
        neatlogicDatasource.setPassword(LocalConfig.dbConfigMap.get("db.password").toString());
        neatlogicDatasource.setPoolName("HikariCP_neatlogicInit");
        setDataSourceConfig(neatlogicDatasource);
    }

    public static Connection getNeatlogicConnection() {
        Connection connection;
        try {
            connection = neatlogicDatasource.getConnection();
        } catch (Exception exception) {
            logger.error(exception.getMessage(), exception);
            throw new ModuleInitRuntimeException("ERROR: " + I18nUtils.getStaticMessage("nfb.moduleinitializer.getactivetenantlist.neatlogicdb", LocalConfig.getPropertiesFrom()), exception);
        }
        return connection;
    }

    public static Connection getNeatlogicTenantConnection(TenantVo tenantVo, boolean isData) {
        Connection connection;
        NeatLogicBasicDataSource tenantDataSource;
        if (datasourceMap.containsKey(tenantVo.getUuid())) {
            tenantDataSource = (NeatLogicBasicDataSource) datasourceMap.get(tenantVo.getUuid());
        } else {
            tenantDataSource = new NeatLogicBasicDataSource();
            String url = tenantVo.getDatasource().getUrl();
            url = url.replace("{host}", tenantVo.getDatasource().getHost());
            url = url.replace("{port}", tenantVo.getDatasource().getPort().toString());
            url = url.replace("{dbname}", "neatlogic_" + tenantVo.getUuid() + (isData ? "_data" : StringUtils.EMPTY));
            //tenantDatasource.setUrl(url);
            tenantDataSource.setJdbcUrl(url);
            tenantDataSource.setDriverClassName(tenantVo.getDatasource().getDriver());
            tenantDataSource.setUsername(tenantVo.getDatasource().getUsername());
            tenantDataSource.setPassword(tenantVo.getDatasource().getPasswordPlain());
            tenantDataSource.setPoolName("HikariCP_" + (isData ? "DATA_" : StringUtils.EMPTY) + tenantVo.getDatasource().getTenantUuid() + "Init");
            setDataSourceConfig(tenantDataSource);
            datasourceMap.put(tenantVo.getUuid(), tenantDataSource);
        }
        try {
            connection = tenantDataSource.getConnection();
        } catch (Exception exception) {
            logger.error(exception.getMessage(), exception);
            throw new ModuleInitRuntimeException("ERROR: " + I18nUtils.getStaticMessage("nfs.scriptrunnermanager.runscriptoncewithjdbc.tenantnotconnect", tenantVo.getUuid()));
        }
        return connection;
    }

    public static void setDataSourceConfig(NeatLogicBasicDataSource dataSource) {
        dataSource.setMaximumPoolSize(50);
        //以下是针对Mysql的参数优化
        //This sets the number of prepared statements that the MySQL driver will cache per connection. The default is a conservative 25. We recommend setting this to between 250-500.
        dataSource.addDataSourceProperty("prepStmtCacheSize", 250);
        //This is the maximum length of a prepared SQL statement that the driver will cache. The MySQL default is 256. In our experience, especially with ORM frameworks like Hibernate, this default is well below the threshold of generated statement lengths. Our recommended setting is 2048.
        dataSource.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);
        //Neither of the above parameters have any effect if the cache is in fact disabled, as it is by default. You must set this parameter to true.
        dataSource.addDataSourceProperty("cachePrepStmts", true);
        //Newer versions of MySQL support server-side prepared statements, this can provide a substantial performance boost. Set this property to true.
        dataSource.addDataSourceProperty("useServerPrepStmts", true);
        dataSource.addDataSourceProperty("useLocalSessionState", true);
        dataSource.addDataSourceProperty("rewriteBatchedStatements", true);
        dataSource.addDataSourceProperty("cacheResultSetMetadata", true);
        dataSource.addDataSourceProperty("cacheServerConfiguration", true);
        dataSource.addDataSourceProperty("elideSetAutoCommits", true);
        dataSource.addDataSourceProperty("maintainTimeStats", false);
        dataSource.setConnectionTimeout(5000);
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

    public static void closeDataSource() {
        neatlogicDatasource.close();
        for (Object o : datasourceMap.values()) {
            ((NeatLogicBasicDataSource) o).close();
        }
    }
}