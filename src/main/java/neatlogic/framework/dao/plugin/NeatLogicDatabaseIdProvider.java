package neatlogic.framework.dao.plugin;

import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.mapping.DatabaseIdProvider;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

public class NeatLogicDatabaseIdProvider implements DatabaseIdProvider {

    private final String MYSQL = "MySQL";
    private final String TIDB = "TiDB";
    private final String OCEAN_BASE = "OceanBase";

    private String databaseId;
    private Properties properties;

    public NeatLogicDatabaseIdProvider() {

    }
    @Override
    public void setProperties(Properties p) {
        this.properties = p;
    }

    @Override
    public String getDatabaseId(DataSource dataSource) throws SQLException {
        if (databaseId != null) {
            return databaseId;
        }
        if (dataSource == null) {
            throw new NullPointerException("dataSource cannot be null");
        }
        try {
            databaseId = getDatabaseName(dataSource);
        } catch (Exception e) {
            LogHolder.log.error("Could not get a databaseId from dataSource", e);
        }
        return databaseId;
    }

    private String getDatabaseName(DataSource dataSource) throws SQLException {
        String productName = getDatabaseProductName(dataSource);
        if (this.properties != null) {
            for (Map.Entry<Object, Object> property : properties.entrySet()) {
                if (productName.contains((String) property.getKey())) {
                    return (String) property.getValue();
                }
            }
            // no match, return null
            return null;
        }
        return productName;
    }

    private String getDatabaseProductName(DataSource dataSource) throws SQLException {
        try (Connection con = dataSource.getConnection()) {
            DatabaseMetaData metaData = con.getMetaData();
            String databaseProductName = metaData.getDatabaseProductName();
            if (Objects.equals(databaseProductName, MYSQL)) {
                Statement statement = null;
                ResultSet resultSet = null;
                try {
                    statement = con.createStatement();
                    resultSet = statement.executeQuery("SELECT @@version");
                    while (resultSet.next()) {
                        String databaseProductVersion = resultSet.getString(1);
                        if (databaseProductVersion.contains(TIDB)) {// 8.0.11-TiDB-v7.4.0
                            databaseProductName = TIDB;
                        } else if (databaseProductVersion.contains(OCEAN_BASE)) {// 5.7.25-OceanBase_CE-v4.2.0.0
                            databaseProductName = OCEAN_BASE;
                        }
                        break;
                    }
                } catch (Exception e) {
                    LogHolder.log.error(e.getMessage(), e);
                } finally {
                    if (resultSet != null) {
                        resultSet.close();
                    }
                    if (statement != null) {
                        statement.close();
                    }
                }
            }
            return databaseProductName;
        }
    }

    private static class LogHolder {
        private static final Log log = LogFactory.getLog(NeatLogicDatabaseIdProvider.class);
    }
}
