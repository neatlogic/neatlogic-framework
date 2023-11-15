package neatlogic.framework.dao.plugin;

import neatlogic.framework.store.mysql.DatabaseVendor;
import neatlogic.framework.store.mysql.DatasourceManager;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.mapping.DatabaseIdProvider;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Objects;

public class NeatLogicDatabaseIdProvider implements DatabaseIdProvider {

    private String databaseId;

    public NeatLogicDatabaseIdProvider() {

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
            DatasourceManager.setDatabaseId(databaseId);
        } catch (Exception e) {
            LogHolder.log.error("Could not get a databaseId from dataSource", e);
        }
        return databaseId;
    }

    private String getDatabaseName(DataSource dataSource) throws SQLException {
        DatabaseVendor vendor = getDatabaseVendor(dataSource);
        if (vendor != null) {
            return vendor.getAlias();
        }
        return null;
    }

    private DatabaseVendor getDatabaseVendor(DataSource dataSource) throws SQLException {
        try (Connection con = dataSource.getConnection()) {
            DatabaseVendor vendor = null;
            DatabaseMetaData metaData = con.getMetaData();
            String databaseProductName = metaData.getDatabaseProductName();
            if (Objects.equals(databaseProductName, DatabaseVendor.MYSQL.getName())) {
                Statement statement = null;
                ResultSet resultSet = null;
                try {
                    statement = con.createStatement();
                    resultSet = statement.executeQuery("SELECT @@version");
                    while (resultSet.next()) {
                        String databaseProductVersion = resultSet.getString(1);
                        if (databaseProductVersion.contains(DatabaseVendor.TIDB.getName())) {// 8.0.11-TiDB-v7.4.0
                            vendor = DatabaseVendor.TIDB;
                        } else if (databaseProductVersion.contains(DatabaseVendor.OCEAN_BASE.getName())) {// 5.7.25-OceanBase_CE-v4.2.0.0
                            vendor = DatabaseVendor.OCEAN_BASE;
                        } else {
                            vendor = DatabaseVendor.MYSQL;
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
            return vendor;
        }
    }

    private static class LogHolder {
        private static final Log log = LogFactory.getLog(NeatLogicDatabaseIdProvider.class);
    }
}
