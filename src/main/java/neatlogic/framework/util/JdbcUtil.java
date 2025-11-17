package neatlogic.framework.util;

import neatlogic.framework.common.config.Config;
import neatlogic.framework.common.config.LocalConfig;
import neatlogic.framework.dto.TenantVo;
import neatlogic.framework.exception.module.ModuleInitRuntimeException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;

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
            props.setProperty("socketTimeout", Config.CHANGELOG_JDBC_SOCKETTIME());
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
            props.setProperty("socketTimeout", Config.CHANGELOG_JDBC_SOCKETTIME());
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

    /**
     *
     * @param sql
     * @param neatlogicConn
     * @return
     * @throws SQLException
     */
    public static List<Map<String, Object>> selectList(String sql, Connection neatlogicConn) throws SQLException {
        try (
                PreparedStatement preparedStatement = neatlogicConn.prepareStatement(sql);
                ResultSet resultSet = preparedStatement.executeQuery();
        ) {
            List<Map<String, Object>> resultList = new ArrayList<>();
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            while (resultSet.next()) {
                Map<String, Object> rowMap = new LinkedHashMap<>();
                for (int i = 1; i <=columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    Object columnValue = resultSet.getObject(i);
                    rowMap.put(columnName, columnValue);
                }
                resultList.add(rowMap);
            }
            return resultList;
        }
    }

    /**
     * 数据可视化
     * +----+----+
     * |a   |b   |
     * +----+----+
     * |A   |B   |
     * +----+----+
     * @param sql
     * @param tbodyList
     * @return
     */
    public static String dataVisualization(String sql, List<Map<String, Object>> tbodyList) {
        sql = sql != null ? sql : StringUtils.EMPTY;
        tbodyList = tbodyList != null ? tbodyList : new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();
        Map<String, Integer> columnName2valueMaxLengthMap = new LinkedHashMap<>();
        if (CollectionUtils.isNotEmpty(tbodyList)) {
            for (Map<String, Object> tbody : tbodyList) {
                for (Map.Entry<String, Object> entry : tbody.entrySet()) {
                    String columnName = entry.getKey();
                    Object columnValue = entry.getValue();
                    int length = columnName.length();
                    if (columnValue != null) {
                        length = Math.max(columnValue.toString().length(), length);
                    }
                    Integer maxLength = columnName2valueMaxLengthMap.getOrDefault(columnName, 0);
                    maxLength = Math.max(maxLength, length);
                    columnName2valueMaxLengthMap.put(columnName, maxLength);
                }
            }
            int length = generateRowDividingLine(stringBuilder, columnName2valueMaxLengthMap);
            int maxLength = length - 4;
            while(StringUtils.isNotBlank(sql)) {
                if (sql.length() > maxLength) {
                    generateSqlRow(stringBuilder, sql.substring(0, maxLength), maxLength);
                    sql = sql.substring(maxLength);
                } else {
                    generateSqlRow(stringBuilder, sql, maxLength);
                    break;
                }
            }
            generateRowDividingLine(stringBuilder, columnName2valueMaxLengthMap);
            generateRowData(stringBuilder, columnName2valueMaxLengthMap, null);
            generateRowDividingLine(stringBuilder, columnName2valueMaxLengthMap);
            for (Map<String, Object> map : tbodyList) {
                generateRowData(stringBuilder, columnName2valueMaxLengthMap, map);
            }
            generateRowDividingLine(stringBuilder, columnName2valueMaxLengthMap);
        } else {
            columnName2valueMaxLengthMap.put("sql", sql.length());
            int length = generateRowDividingLine(stringBuilder, columnName2valueMaxLengthMap);
            int maxLength = length - 4;
            generateSqlRow(stringBuilder, sql, maxLength);
            generateRowDividingLine(stringBuilder, columnName2valueMaxLengthMap);
            String str = "No data";
            stringBuilder.append("| ");
            stringBuilder.append(str);
            stringBuilder.append(" ".repeat(maxLength - str.length() + 1));
            stringBuilder.append("|");
            stringBuilder.append(System.lineSeparator());
            generateRowDividingLine(stringBuilder, columnName2valueMaxLengthMap);
        }
        stringBuilder.append(tbodyList.size()).append(" rows in set").append(System.lineSeparator());
        stringBuilder.insert(0, System.lineSeparator());
        return stringBuilder.toString();
    }

    private static int generateRowDividingLine(StringBuilder stringBuilder, Map<String, Integer> columnName2valueMaxLengthMap) {
        for (Map.Entry<String, Integer> entry : columnName2valueMaxLengthMap.entrySet()) {
            Integer maxLength = entry.getValue();
            stringBuilder.append("+-");
            stringBuilder.append("-".repeat(maxLength + 1));
        }
        stringBuilder.append("+");
        int length = stringBuilder.length();
        stringBuilder.append(System.lineSeparator());
        return length;
    }

    private static void generateSqlRow(StringBuilder stringBuilder, String sql, int maxLength) {
        stringBuilder.append("| ");
        stringBuilder.append(sql);
        stringBuilder.append(" ".repeat(maxLength - sql.length() + 1));
        stringBuilder.append("|");
        stringBuilder.append(System.lineSeparator());
    }

    private static void generateRowData(StringBuilder stringBuilder, Map<String, Integer> columnName2valueMaxLengthMap, Map<String, Object> map) {
        for (Map.Entry<String, Integer> entry : columnName2valueMaxLengthMap.entrySet()) {
            String key = entry.getKey();
            String valueStr = StringUtils.EMPTY;
            Object valueObj = key;
            if (MapUtils.isNotEmpty(map)) {
                valueObj = map.get(key);
            }
            if (valueObj != null) {
                valueStr = valueObj.toString();
            }
            Integer maxLength = entry.getValue();
            stringBuilder.append("| ");
            stringBuilder.append(valueStr);
            stringBuilder.append(" ".repeat(maxLength - valueStr.length() + 1));
        }
        stringBuilder.append("|");
        stringBuilder.append(System.lineSeparator());
    }

}
