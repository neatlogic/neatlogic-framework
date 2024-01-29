/*
 * Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package neatlogic.framework.sqlfile;

import neatlogic.framework.dao.mapper.TenantMapper;
import neatlogic.framework.dto.ChangelogAuditVo;
import neatlogic.framework.dto.TenantModuleDmlSqlVo;
import neatlogic.framework.dto.TenantVo;
import neatlogic.framework.exception.module.ModuleInitRuntimeException;
import neatlogic.framework.store.mysql.DatasourceManager;
import neatlogic.framework.store.mysql.NeatLogicBasicDataSource;
import neatlogic.framework.util.I18nUtils;
import neatlogic.framework.util.JdbcUtil;
import neatlogic.framework.util.Md5Util;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Component
public class ScriptRunnerManager {
    static Logger logger = LoggerFactory.getLogger(ScriptRunnerManager.class);

    private static TenantMapper tenantMapper;

    @Autowired
    public void setTenantMapper(TenantMapper _tenantMapper) {
        tenantMapper = _tenantMapper;
    }

    /**
     * 执行sql文件
     *
     * @param tenant       租户
     * @param scriptReader 脚本读取
     * @param logWriter    日志
     * @param errWriter    错误日志
     */
    public static void runScript(TenantVo tenant, Reader scriptReader, PrintWriter logWriter, PrintWriter errWriter, boolean isDataDb) throws Exception {
        Connection conn = null;
        ScriptRunner runner = null;
        String tenantUuid = tenant.getUuid();
        if (isDataDb) {
            tenantUuid = tenantUuid + "_data";
        }
        NeatLogicBasicDataSource tenantDatasource = DatasourceManager.getDatasource(tenantUuid);
        try {
            conn = tenantDatasource.getConnection();
            runner = new ScriptRunner(conn);
            runner.setSendFullScript(false);
            runner.setAutoCommit(true);
            // 有错误会继续执行
            runner.setStopOnError(false);
            // Resources.setCharset(Charset.forName("UTF-8"));
            runner.setLogWriter(logWriter);
            runner.setErrorLogWriter(errWriter);
            runner.setDelimiter(";");
            runner.runScript(scriptReader);
        } catch (Exception ex) {
            logger.error("执行ddl sql异常: " + ex.getMessage(), ex);
            throw new Exception(ex);
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
                if(runner != null){
                    runner.closeConnection();
                }
            } catch (SQLException e) {
                logger.error(e.getMessage());
            }
        }
    }

    /**
     * 仅执行一次sql，执行过的sql跳过不执行
     *
     * @param tenant       租户
     * @param moduleId     模块id
     * @param scriptReader 脚本读取
     * @param isDataDb     是否data库
     */
    public static void runScriptOnceWithJdbc(TenantVo tenant, String moduleId, Reader scriptReader, Boolean isDataDb, String type) throws Exception {
        StringWriter logStrWriter = new StringWriter();
        PrintWriter logWriter = new PrintWriter(logStrWriter);
        StringWriter errStrWriter = new StringWriter();
        PrintWriter errWriter = new PrintWriter(errStrWriter);
        PreparedStatement sqlMd5Statement = null;
        ResultSet sqlMd5ResultSet = null;
        Connection conn = null;
        Connection neatlogicConn = null;
        ScriptRunner runner = null;
        BufferedReader scriptBufferedReader = null;
        try {
            List<String> hasRunSqlMd5List = new ArrayList<>();
            neatlogicConn = JdbcUtil.getNeatlogicConnection();
            sqlMd5Statement = neatlogicConn.prepareStatement("select sql_uuid from tenant_module_dmlsql where tenant_uuid = ? and `module_id` = ? and `sql_status` = 1");
            sqlMd5Statement.setString(1, tenant.getUuid());
            sqlMd5Statement.setString(2, moduleId);
            sqlMd5ResultSet = sqlMd5Statement.executeQuery();
            while (sqlMd5ResultSet.next()) {
                hasRunSqlMd5List.add(sqlMd5ResultSet.getString("sql_uuid"));
            }
            scriptBufferedReader = new BufferedReader(scriptReader);
            conn = JdbcUtil.getNeatlogicTenantConnection(tenant, isDataDb);
            runner = new ScriptRunner(conn);
            runner.setLogWriter(logWriter);
            runner.setErrorLogWriter(errWriter);
            runner.setSendFullScript(false);
            runner.setAutoCommit(true);
            String line;
            while ((line = scriptBufferedReader.readLine()) != null) {
                if (StringUtils.isBlank(line.trim())) {
                    continue;
                }
                // 如果没有执行过该sql，则执行
                String sqlMd5 = Md5Util.encryptMD5(line);
                if (!hasRunSqlMd5List.contains(sqlMd5)) {
                    runner.runScript(new StringReader(line));
                    TenantModuleDmlSqlVo tenantModuleDmlSqlVo;
                    if (StringUtils.isNotBlank(errStrWriter.toString())) {
                        String error = "  ✖" + tenant.getName() + "·" + moduleId + "." + type + ": " + errStrWriter;
                        //tenantModuleDmlSqlVo = new TenantModuleDmlSqlVo(tenant.getUuid(), moduleId, sqlMd5, 0, errStrWriter.toString(), type);
                        //errStrWriter.getBuffer().setLength(0);
                        if (StringUtils.isNotBlank(type) && type.endsWith("dml")) {
                            error += I18nUtils.getStaticMessage("nfs.scriptrunnermanager.runscriptoncewithjdbc.failed");
                        }
                        throw new ModuleInitRuntimeException(error);
                    } else {
                        tenantModuleDmlSqlVo = new TenantModuleDmlSqlVo(tenant.getUuid(), moduleId, sqlMd5, type);
                        hasRunSqlMd5List.add(sqlMd5);
                    }
                    insertTenantModuleDmlSql(tenantModuleDmlSqlVo);
                    insertTenantModuleDmlSqlDetail(sqlMd5, line);
                }
            }
        } catch (ModuleInitRuntimeException ex) {
            throw new ModuleInitRuntimeException(ex);
        } catch (Exception ex) {
            logger.error("通过jdbc执行dml sql异常: " + ex.getMessage(), ex);
            throw new Exception(ex);
        } finally {
            try {
                JdbcUtil.closeConnection(conn);
                JdbcUtil.closeConnection(neatlogicConn);
                JdbcUtil.closeStatement(sqlMd5Statement);
                JdbcUtil.closeResultSet(sqlMd5ResultSet);
                if (runner != null) {
                    runner.closeConnection();
                }
                if (scriptBufferedReader != null) {
                    scriptBufferedReader.close();
                }
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
    }

    /**
     * 执行neatlogic Changelog sql
     *
     * @param scriptReader 脚本读取
     */
    public static void runScriptWithJdbc(Reader scriptReader, String version, Connection connection, String sqlFile) throws Exception {
        runScriptWithJdbc(null, "framework", scriptReader, version, connection, sqlFile);
    }

    /**
     * changelog 记录自动忽略不存在，已存在之类的sql异常
     */
    private static final List<String> ignoreKeyList = Arrays.asList("exist", "duplicate");

    /**
     * 执行sql，并记录changelog
     *
     * @param tenant       租户
     * @param moduleId     模块id
     * @param scriptReader 脚本读取
     */
    public static boolean runScriptWithJdbc(TenantVo tenant, String moduleId, Reader scriptReader, String version, Connection conn, String sqlFile) throws Exception {
        ScriptRunner runner = null;
        StringWriter logStrWriter = new StringWriter();
        PrintWriter logWriter = new PrintWriter(logStrWriter);
        StringWriter errStrWriter = new StringWriter();
        PrintWriter errWriter = new PrintWriter(errStrWriter);
        PreparedStatement sqlMd5Statement = null;
        Connection neatlogicConn = null;
        ResultSet sqlMd5ResultSet = null;
        BufferedReader scriptBufferedReader = null;
        String tenantUuid = tenant == null ? "0" : tenant.getUuid(); //主库用0表示
        boolean isError = false;
        try {
            scriptBufferedReader = new BufferedReader(scriptReader);
            List<String> hasRunSqlMd5List = new ArrayList<>();
            neatlogicConn = JdbcUtil.getNeatlogicConnection();
            sqlMd5Statement = neatlogicConn.prepareStatement("select sql_hash from changelog_audit where tenant_uuid = ? and `module_id` = ? and (`sql_status` = 1 or `ignored` = 1) ");
            sqlMd5Statement.setString(1, tenantUuid);
            sqlMd5Statement.setString(2, moduleId);
            sqlMd5ResultSet = sqlMd5Statement.executeQuery();
            while (sqlMd5ResultSet.next()) {
                hasRunSqlMd5List.add(sqlMd5ResultSet.getString("sql_hash"));
            }
            runner = new ScriptRunner(conn);
            runner.setSendFullScript(false);
            runner.setAutoCommit(true);
            // 有错误会继续执行
            runner.setStopOnError(false);
            // Resources.setCharset(Charset.forName("UTF-8"));
            runner.setLogWriter(logWriter);
            runner.setErrorLogWriter(errWriter);
            runner.setDelimiter(";");
            String line;
            StringBuilder sqlSb = new StringBuilder();
            while ((line = scriptBufferedReader.readLine()) != null) {
                if (StringUtils.isBlank(line.trim())) {
                    continue;
                }
                if (!line.trim().toLowerCase(Locale.ROOT).endsWith(";")) {
                    sqlSb.append(line);
                    continue;
                }
                sqlSb.append(line);
                String sql = sqlSb.toString();
                sqlSb.setLength(0);
                // 如果没有执行过该sql，则执行
                String sqlHash = Md5Util.encryptMD5(sql);
                if (!hasRunSqlMd5List.contains(sqlHash)) {
                    runner.runScript(new StringReader(sql));
                    ChangelogAuditVo changelogAuditVo;
                    if (StringUtils.isNotBlank(errStrWriter.toString())) {
                        String error;
                        if (tenant == null) {
                            error = "  ✖" + moduleId + "." + version + "·" + sqlFile + ": " + errStrWriter;
                        } else {
                            error = "  ✖" + tenant.getName() + "·" + moduleId + "." + version + "·" + sqlFile + ": " + errStrWriter;
                        }
                        //tenantModuleDmlSqlVo = new TenantModuleDmlSqlVo(tenant.getUuid(), moduleId, sqlMd5, 0, errStrWriter.toString(), type);
                        int ignored = 0;
                        if (ignoreKeyList.stream().anyMatch(o -> errStrWriter.toString().toLowerCase(Locale.ROOT).contains(o))) {
                            ignored = 1;
                        } else {
                            System.out.println(error);
                            isError = true;
                        }
                        changelogAuditVo = new ChangelogAuditVo(tenantUuid, moduleId, sqlHash, version, error, 0, ignored);
                        insertChangelogAudit(changelogAuditVo);
                        insertChangelogAuditDetail(sqlHash, sql);
                        errStrWriter.getBuffer().setLength(0);
                    } else {
                        changelogAuditVo = new ChangelogAuditVo(tenantUuid, moduleId, sqlHash, version, 1);
                        hasRunSqlMd5List.add(sqlHash);
                        insertChangelogAudit(changelogAuditVo);
                        insertChangelogAuditDetail(sqlHash, sql);
                    }
                }
            }
        } catch (ModuleInitRuntimeException ex) {
            throw new ModuleInitRuntimeException(ex);
        } catch (Exception ex) {
            logger.error("通过jdbc执行sql异常: " + ex.getMessage(), ex);
            throw new Exception(ex);
        } finally {
            try {
                JdbcUtil.closeConnection(conn);
                JdbcUtil.closeConnection(neatlogicConn);
                JdbcUtil.closeStatement(sqlMd5Statement);
                JdbcUtil.closeResultSet(sqlMd5ResultSet);
                if (runner != null) {
                    runner.closeConnection();
                }
                if (scriptBufferedReader != null) {
                    scriptBufferedReader.close();
                }
            } catch (Exception e) {
                logger.error(e.getMessage());
            }

        }
        return isError;
    }

    /**
     * 记录租户changelog记录
     *
     * @param changelogAuditVo changelog记录
     */
    private static void insertChangelogAudit(ChangelogAuditVo changelogAuditVo) throws Exception {
        try (Connection neatlogicConn = JdbcUtil.getNeatlogicConnection(); PreparedStatement statement = neatlogicConn.prepareStatement("insert into `changelog_audit` (`tenant_uuid`,`module_id`,`sql_hash`,`version`,`error_msg`,`sql_status`,`lcd`,`ignored`) VALUES (?,?,?,?,?,?,now(),?) ON DUPLICATE KEY UPDATE `error_msg` = ?,`sql_status` = ?, `lcd` = now(),`ignored` = ? ")) {
            statement.setString(1, changelogAuditVo.getTenantUuid());
            statement.setString(2, changelogAuditVo.getModuleId());
            statement.setString(3, changelogAuditVo.getSqlHash());
            statement.setString(4, changelogAuditVo.getVersion());
            statement.setString(5, changelogAuditVo.getErrorMsg());
            statement.setInt(6, changelogAuditVo.getSqlStatus());
            statement.setInt(7, changelogAuditVo.getIgnored());
            statement.setString(8, changelogAuditVo.getErrorMsg());
            statement.setInt(9, changelogAuditVo.getSqlStatus());
            statement.setInt(10, changelogAuditVo.getIgnored());
            statement.execute();
        } catch (Exception ex) {
            throw new Exception(ex);
        }
    }


    /**
     * 记录changelog sql语句
     *
     * @param hash sql的哈希唯一值
     * @param sql  sql语句
     */
    private static void insertChangelogAuditDetail(String hash, String sql) throws Exception {
        try (Connection neatlogicConn = JdbcUtil.getNeatlogicConnection(); PreparedStatement statement = neatlogicConn.prepareStatement("insert ignore into `changelog_audit_detail` (`hash`,`sql`) VALUES (?,?) ");) {
            statement.setString(1, hash);
            statement.setString(2, sql);
            statement.execute();
        } catch (Exception ex) {
            throw new Exception(ex);
        }
    }

    /**
     * 记录租户dml sql执行记录
     *
     * @param tenantModuleDmlSqlVo dml sql对象
     */
    private static void insertTenantModuleDmlSql(TenantModuleDmlSqlVo tenantModuleDmlSqlVo) throws Exception {
        try (Connection neatlogicConn = JdbcUtil.getNeatlogicConnection(); PreparedStatement statement = neatlogicConn.prepareStatement("insert into `tenant_module_dmlsql` (`tenant_uuid`,`module_id`,`sql_uuid`,`sql_status`,`error_msg`,`fcd`,`type`) VALUES (?,?,?,?,?,now(),?) ON DUPLICATE KEY UPDATE `sql_status` = ? , `error_msg` = ?")) {
            statement.setString(1, tenantModuleDmlSqlVo.getTenantUuid());
            statement.setString(2, tenantModuleDmlSqlVo.getModuleId());
            statement.setString(3, tenantModuleDmlSqlVo.getSqlMd5());
            statement.setInt(4, tenantModuleDmlSqlVo.getSqlStatus());
            statement.setString(5, tenantModuleDmlSqlVo.getErrorMsg());
            statement.setString(6, tenantModuleDmlSqlVo.getType());
            statement.setInt(7, tenantModuleDmlSqlVo.getSqlStatus());
            statement.setString(8, tenantModuleDmlSqlVo.getErrorMsg());
            statement.execute();
        } catch (Exception ex) {
            throw new Exception(ex);
        }
    }

    /**
     * 记录租户dml sql语句
     *
     * @param md5    sql的哈希唯一值
     * @param dmlSql sql语句
     */
    private static void insertTenantModuleDmlSqlDetail(String md5, String dmlSql) throws Exception {
        try (Connection neatlogicConn = JdbcUtil.getNeatlogicConnection(); PreparedStatement statement = neatlogicConn.prepareStatement("insert ignore into `tenant_module_dmlsql_detail` (`hash`,`sql`) VALUES (?,?) ")) {
            statement.setString(1, md5);
            statement.setString(2, dmlSql);
            statement.execute();
        } catch (Exception ex) {
            throw new Exception(ex);
        }
    }

    /**
     * 清楚异常dml sql记录
     *
     * @param tenantUuid    租户
     * @param moduleId      模块
     * @param neatlogicConn 连接neatlogic库 connection
     */
    private static void clearTenantModuleDmlSqlError(String tenantUuid, String moduleId, Connection neatlogicConn) throws Exception {
        PreparedStatement statement = null;
        try {
            String sql = "delete from `tenant_module_dmlsql` where `tenant_uuid` = ? and `module_id` = ? and `sql_status` = 0 ";
            statement = neatlogicConn.prepareStatement(sql);
            statement.setString(1, tenantUuid);
            statement.setString(2, moduleId);
            statement.execute();
        } catch (Exception ex) {
            throw new Exception(ex);
        } finally {
            JdbcUtil.closeStatement(statement);
        }
    }
}
