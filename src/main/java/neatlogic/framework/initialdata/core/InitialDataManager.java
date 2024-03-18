/*Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.*/

package neatlogic.framework.initialdata.core;

import neatlogic.framework.common.dto.ValueTextVo;
import neatlogic.framework.dto.module.ImportResultVo;
import neatlogic.framework.exception.module.InitialDataFileIrregularException;
import neatlogic.framework.exception.module.TableIsNotEmptyException;
import neatlogic.framework.util.Md5Util;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

@Service
public class InitialDataManager {
    private static DataSource dataSource;

    @Resource(name = "dataSource")
    public void setDataSource(DataSource _dataSource) {
        dataSource = _dataSource;
    }

    private static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    static Logger logger = LoggerFactory.getLogger(InitialDataManager.class);
    private static final Map<String, String[]> moduleTableMap = new HashMap<>();

    static {
        Reflections reflections = new Reflections("neatlogic");
        Set<Class<? extends IInitialDataDefiner>> sourceClass = reflections.getSubTypesOf(IInitialDataDefiner.class);
        for (Class<? extends IInitialDataDefiner> c : sourceClass) {
            try {
                IInitialDataDefiner definer = c.newInstance();
                moduleTableMap.put(definer.getModuleId(), definer.getTables());
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    public static boolean hasInitialData(String moduleId) {
        return moduleTableMap.containsKey(moduleId);
    }

    public static List<ValueTextVo> validData(String moduleId, InputStream is) throws IOException {
        ZipInputStream zin = new ZipInputStream(is);
        ZipEntry entry;
        List<ValueTextVo> sqlList = new ArrayList<>();
        boolean isValid = false;
        while ((entry = zin.getNextEntry()) != null) {
            if (entry.getName().equals("checksum")) {
                StringWriter writer = new StringWriter();
                IOUtils.copy(zin, writer, StandardCharsets.UTF_8.name());
                String checksum = writer.toString();
                if (StringUtils.isNotBlank(checksum) && checksum.equals(Md5Util.encryptMD5(moduleId))) {
                    isValid = true;
                }
            } else {
                String tableName = entry.getName().replace(".sql", "");
                sqlList.add(new ValueTextVo(tableName, "导入" + tableName + "数据"));
            }
        }
        if (!isValid) {
            throw new InitialDataFileIrregularException();
        }
        IAfterInitialDataImportHandler handler = AfterInitialDataImportHandlerFactory.getHandler(moduleId);
        if (handler != null) {
            sqlList.add(new ValueTextVo("#afterall", handler.getDescription()));
        }
        return sqlList;
    }

    public static List<ImportResultVo> importData(String moduleId, InputStream is) throws SQLException {
        ZipInputStream zin = new ZipInputStream(is);
        List<ImportResultVo> resultList = new ArrayList<>();
        ZipEntry entry;
        Connection conn = null;
        PreparedStatement stmt = null;
        Boolean defaultAutoCommit = null;
        String currentTable = "";
        boolean isFailed = false;
        try {
            conn = getConnection();
            defaultAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);
            boolean isValid = false;
            while ((entry = zin.getNextEntry()) != null) {
                if (entry.getName().equals("checksum")) {
                    StringWriter writer = new StringWriter();
                    IOUtils.copy(zin, writer, StandardCharsets.UTF_8.name());
                    String checksum = writer.toString();
                    if (StringUtils.isNotBlank(checksum) && checksum.equals(Md5Util.encryptMD5(moduleId))) {
                        isValid = true;
                    }
                } else {
                    String table = entry.getName().replace(".sql", "");
                    currentTable = table;
                    StringWriter writer = new StringWriter();
                    IOUtils.copy(zin, writer, StandardCharsets.UTF_8.name());
                    String sql = writer.toString();
                    String checkSql = getCheckSql(table);
                    stmt = conn.prepareStatement(checkSql);
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        if (rs.getInt(1) > 0) {
                            throw new TableIsNotEmptyException(table);
                        }
                    }
                    rs.close();
                    if (StringUtils.isNotBlank(sql)) {
                        stmt = conn.prepareStatement(sql);
                        stmt.execute();
                        stmt.close();
                    }
                    resultList.add(new ImportResultVo(table, "success", ""));
                }
            }
            if (!isValid) {
                throw new InitialDataFileIrregularException();
            }
            conn.commit();
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            if (conn != null) {
                conn.rollback();
            }
            resultList.add(new ImportResultVo(currentTable, "failed", ex.getMessage()));
            isFailed = true;
        } finally {
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                if (defaultAutoCommit != null) {
                    conn.setAutoCommit(defaultAutoCommit);
                }
                conn.close();
            }
        }
        if (!isFailed) {
            IAfterInitialDataImportHandler handler = AfterInitialDataImportHandlerFactory.getHandler(moduleId);
            if (handler != null) {
                try {
                    handler.execute();
                    resultList.add(new ImportResultVo("#afterall", "success", ""));
                } catch (Exception ex) {
                    resultList.add(new ImportResultVo("#afterall", "failed", ex.getMessage()));
                }
            }
        }
        return resultList;
    }

    public static void exportData(String moduleId, OutputStream os) throws SQLException, IOException {
        ZipOutputStream zos = new ZipOutputStream(os);
        // 仅打包归档存储
        zos.setMethod(ZipOutputStream.DEFLATED);
        zos.setLevel(0);

        if (moduleTableMap.containsKey(moduleId)) {
            String[] tables = moduleTableMap.get(moduleId);
            if (tables != null && tables.length > 0) {
                //加入checksum文件
                InputStream checksumIs = new ByteArrayInputStream(Md5Util.encryptMD5(moduleId).getBytes(StandardCharsets.UTF_8));
                zos.putNextEntry(new ZipEntry("checksum"));
                IOUtils.copy(checksumIs, zos);
                zos.closeEntry();
                for (String table : tables) {
                    List<String> sqlList = exportTable(table);
                    InputStream is = new ByteArrayInputStream(String.join("", sqlList).getBytes(StandardCharsets.UTF_8));
                    zos.putNextEntry(new ZipEntry(table + ".sql"));
                    IOUtils.copy(is, zos);
                    zos.closeEntry();
                }
            }
        }
        zos.close();
    }

    private static String getCheckSql(String table) {
        return "SELECT count(1) FROM `" + table + "`";
    }

    private static String getSelectSql(String table) {
        return "SELECT * FROM `" + table + "`";
    }

    private static String buildInsertSql(String table, ResultSetMetaData metaData, ResultSet resultSet) throws SQLException {
        StringBuilder insertSql = new StringBuilder("INSERT INTO `" + table + "` (");
        int columnCount = metaData.getColumnCount();
        for (int i = 1; i <= columnCount; i++) {
            if (i > 1) {
                insertSql.append(",");
            }
            insertSql.append("`").append(metaData.getColumnLabel(i)).append("`");
        }
        insertSql.append(") VALUES (");
        for (int i = 1; i <= columnCount; i++) {
            Object v = resultSet.getObject(i);
            if (i > 1) {
                insertSql.append(",");
            }
            if (v == null) {
                insertSql.append("NULL");
            } else if (v instanceof String) {
                insertSql.append("'").append(v).append("'");
            } else if (v instanceof Number) {
                insertSql.append(v);
            } else if (v instanceof Boolean) {
                insertSql.append((Boolean) v ? 1 : 0);
            } else {
                insertSql.append("'").append(v).append("'");
            }
        }
        insertSql.append(");\n");
        return insertSql.toString();
    }


    private static List<String> exportTable(String table) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet resultSet = null;
        List<String> sqlList = new ArrayList<>();
        try {
            conn = getConnection();
            stmt = conn.prepareStatement(getSelectSql(table));
            resultSet = stmt.executeQuery();
            ResultSetMetaData metaData = resultSet.getMetaData();
            while (resultSet.next()) {
                sqlList.add(buildInsertSql(table, metaData, resultSet));
            }
        } finally {
            if (resultSet != null) {
                resultSet.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
        return sqlList;
    }
}
