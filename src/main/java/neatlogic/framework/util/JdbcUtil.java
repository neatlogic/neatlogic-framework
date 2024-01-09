package neatlogic.framework.util;

import neatlogic.framework.common.config.LocalConfig;
import neatlogic.framework.dto.DatasourceVo;
import neatlogic.framework.dto.TenantVo;
import neatlogic.framework.exception.module.ModuleInitRuntimeException;
import neatlogic.framework.store.mysql.DatasourceManager;
import neatlogic.framework.store.mysql.NeatLogicBasicDataSource;
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
        neatlogicDatasource.setPoolName("HikariCP_neatlogic");
        DatasourceManager.setDataSourceConfig(neatlogicDatasource);
    }

    public static Connection getNeatlogicConnection() {
        Connection connection;
        try {
            connection = neatlogicDatasource.getConnection();
        } catch (Exception exception) {
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
            DatasourceVo datasource = new DatasourceVo();
            datasource.setHost(tenantVo.getDatasource().getHost());
            datasource.setTenantUuid(tenantVo.getUuid());
            datasource.setPort(tenantVo.getDatasource().getPort());
            datasource.setDriver(tenantVo.getDatasource().getDriver());
            datasource.setUrl(tenantVo.getDatasource().getUrl());
            datasource.setUsername(tenantVo.getDatasource().getUsername());
            datasource.setPasswordPlain(tenantVo.getDatasource().getPasswordPlain());
            tenantDataSource = DatasourceManager.getDataSource(datasource, isData);
            datasourceMap.put(tenantVo.getUuid(), tenantDataSource);
        }
        try {
            connection = tenantDataSource.getConnection();
        } catch (Exception exception) {
            throw new ModuleInitRuntimeException("ERROR: " + I18nUtils.getStaticMessage("nfs.scriptrunnermanager.runscriptoncewithjdbc.tenantnotconnect", tenantVo.getUuid()));
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