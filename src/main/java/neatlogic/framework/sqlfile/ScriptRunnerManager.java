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

package neatlogic.framework.sqlfile;

import neatlogic.framework.dao.mapper.TenantMapper;
import neatlogic.framework.dto.ChangelogAuditVo;
import neatlogic.framework.dto.ExecuteSqlParamVo;
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
import java.sql.SQLException;
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
            } catch (SQLException e) {
                logger.error(e.getMessage());
            }
        }
    }

    /**
     * 仅执行一次sql，执行过的sql跳过不执行
     */
    public static void runScriptOnceWithJdbc(ExecuteSqlParamVo executeSqlParamVo) throws Exception {
        StringWriter logStrWriter = new StringWriter();
        PrintWriter logWriter = new PrintWriter(logStrWriter);
        StringWriter errStrWriter = new StringWriter();
        PrintWriter errWriter = new PrintWriter(errStrWriter);
        ScriptRunner runner = null;
        BufferedReader scriptBufferedReader = null;
        try {
            Reader scriptReader = new InputStreamReader(executeSqlParamVo.getResource().getInputStream());
            scriptBufferedReader = new BufferedReader(scriptReader);
            runner = new ScriptRunner(executeSqlParamVo.getNeatlogicTenantConn());
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
                if (!executeSqlParamVo.getDmlSqlHashList().contains(sqlMd5)) {
                    runner.runScript(new StringReader(line));
                    TenantModuleDmlSqlVo tenantModuleDmlSqlVo;
                    if (StringUtils.isNotBlank(errStrWriter.toString())) {
                        String error = "  ✖" + executeSqlParamVo.getTenant().getName() + "·" + executeSqlParamVo.getModuleId() + "." + executeSqlParamVo.getSqlFile() + ": " + errStrWriter;
                        //tenantModuleDmlSqlVo = new TenantModuleDmlSqlVo(tenant.getUuid(), moduleId, sqlMd5, 0, errStrWriter.toString(), type);
                        //errStrWriter.getBuffer().setLength(0);
                        if (StringUtils.isNotBlank(executeSqlParamVo.getSqlFile()) && executeSqlParamVo.getSqlFile().equals("dml.sql")) {
                            error += I18nUtils.getStaticMessage("nfs.scriptrunnermanager.runscriptoncewithjdbc.failed");
                        }
                        throw new ModuleInitRuntimeException(error);
                    } else {
                        tenantModuleDmlSqlVo = new TenantModuleDmlSqlVo(executeSqlParamVo.getTenant().getUuid(), executeSqlParamVo.getModuleId(), sqlMd5, executeSqlParamVo.getSqlFile());
                        executeSqlParamVo.getDmlSqlHashList().add(sqlMd5);
                    }
                    insertTenantModuleDmlSql(tenantModuleDmlSqlVo, executeSqlParamVo.getNeatlogicConn());
                    insertTenantModuleDmlSqlDetail(sqlMd5, line, executeSqlParamVo.getNeatlogicConn());
                }
            }
        } catch (ModuleInitRuntimeException ex) {
            throw new ModuleInitRuntimeException(ex);
        } catch (Exception ex) {
            logger.error("通过jdbc执行dml sql异常: " + ex.getMessage(), ex);
            throw new Exception(ex);
        } finally {
            try {
                if (scriptBufferedReader != null) {
                    scriptBufferedReader.close();
                }
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
    }

    /**
     * changelog 记录自动忽略不存在，已存在之类的sql异常
     */
    private static final List<String> ignoreKeyList = Arrays.asList("exist", "duplicate");

    /**
     * 执行sql，并记录changelog
     */
    public static boolean runScriptWithJdbc(ExecuteSqlParamVo executeSqlParamVo) throws Exception {
        StringWriter errStrWriter = new StringWriter();
        PrintWriter errWriter = new PrintWriter(errStrWriter);
        StringWriter logStrWriter = new StringWriter();
        PrintWriter logWriter = new PrintWriter(logStrWriter);
        BufferedReader scriptBufferedReader = null;
        try {
            Reader scriptReader = new InputStreamReader(executeSqlParamVo.getResource().getInputStream());
            scriptBufferedReader = new BufferedReader(scriptReader);
            String line;
            StringBuilder sqlSb = new StringBuilder();
            executeSqlParamVo.setLogWriter(logWriter);
            executeSqlParamVo.setErrWriter(errWriter);
            executeSqlParamVo.setErrStrWriter(errStrWriter);
            if(executeSqlParamVo.getRunner() != null) {
                executeSqlParamVo.getRunner().setDelimiter(";");
            }
            while ((line = scriptBufferedReader.readLine()) != null) {
                if (StringUtils.isBlank(line.trim())) {
                    continue;
                }
                //自定义分隔符，如：$$ 兼容存储过程
                if (line.trim().toLowerCase(Locale.ROOT).startsWith("delimiter")) {
                    executeSqlParamVo.setDelimiter(line.substring(9).trim());
                    if(executeSqlParamVo.getRunner() != null) {
                        executeSqlParamVo.getRunner().setDelimiter(line.substring(9).trim());
                    }
                    continue;
                } else if (!line.trim().toLowerCase(Locale.ROOT).endsWith(executeSqlParamVo.getDelimiter())) {
                    sqlSb.append("\n");
                    sqlSb.append(line);
                    continue;
                }

                sqlSb.append(line);
                String sql = sqlSb.toString();
                if (!executeSqlParamVo.isAll()) {
                    //清空sql
                    sqlSb.setLength(0);
                    executeSqlParamVo.setSql(sql);
                    executeSqlParamVo.setError(executeSql(executeSqlParamVo));
                }
            }
            if (executeSqlParamVo.isAll()) {
                String sql = sqlSb.toString();
                executeSqlParamVo.setSql(sql);
                executeSqlParamVo.setError(executeSql(executeSqlParamVo));
            }
        } catch (ModuleInitRuntimeException ex) {
            throw new ModuleInitRuntimeException(ex);
        } catch (Exception ex) {
            logger.error("通过jdbc执行sql异常: " + ex.getMessage(), ex);
            throw new Exception(ex);
        } finally {
            if (scriptBufferedReader != null) {
                scriptBufferedReader.close();
            }
        }
        return executeSqlParamVo.isError();
    }

    private static void initRunner(ExecuteSqlParamVo executeSqlParamVo) {
        ScriptRunner runner = new ScriptRunner(executeSqlParamVo.getConn());
        runner.setSendFullScript(false);
        runner.setAutoCommit(true);
        // 有错误会继续执行
        runner.setStopOnError(false);
        // Resources.setCharset(Charset.forName("UTF-8"));
        runner.setLogWriter(executeSqlParamVo.getLogWriter());
        runner.setErrorLogWriter(executeSqlParamVo.getErrWriter());
//            runner.setDelimiter(";");
        runner.setDelimiter(executeSqlParamVo.getDelimiter());
        executeSqlParamVo.setRunner(runner);
    }

    /**
     * 执行sql
     */
    private static boolean executeSql(ExecuteSqlParamVo executeSqlParamVo) throws Exception {
        String tenantUuid = executeSqlParamVo.getTenant() == null ? "0" : executeSqlParamVo.getTenant().getUuid(); //主库用0表示
        // 如果没有执行过该sql，则执行
        String sqlHash = Md5Util.encryptMD5(executeSqlParamVo.getSql());
        if (!executeSqlParamVo.getChangelogSqlHashList().contains(sqlHash)) {
            if (executeSqlParamVo.getRunner() == null) {
                initRunner(executeSqlParamVo);
            }
            executeSqlParamVo.getRunner().runScript(new StringReader(executeSqlParamVo.getSql()));
            ChangelogAuditVo changelogAuditVo;
            if (StringUtils.isNotBlank(executeSqlParamVo.getErrStrWriter().toString())) {
                String error;
                if (executeSqlParamVo.getTenant() == null) {
                    error = "  ✖" + executeSqlParamVo.getModuleId() + "." + executeSqlParamVo.getVersion() + "·" + executeSqlParamVo.getSqlFile() + ": " + executeSqlParamVo.getErrStrWriter();
                } else {
                    error = "  ✖" + executeSqlParamVo.getTenant().getName() + "·" + executeSqlParamVo.getModuleId() + "." + executeSqlParamVo.getVersion() + "·" + executeSqlParamVo.getSqlFile() + ": " + executeSqlParamVo.getErrStrWriter();
                }
                //tenantModuleDmlSqlVo = new TenantModuleDmlSqlVo(tenant.getUuid(), moduleId, sqlMd5, 0, errStrWriter.toString(), type);
                int ignored = 0;
                if (ignoreKeyList.stream().anyMatch(o -> executeSqlParamVo.getErrStrWriter().toString().toLowerCase(Locale.ROOT).contains(o))) {
                    ignored = 1;
                } else {
                    System.out.println(error);
                    executeSqlParamVo.setError(true);
                }
                changelogAuditVo = new ChangelogAuditVo(tenantUuid, executeSqlParamVo.getModuleId(), sqlHash, executeSqlParamVo.getVersion(), error, 0, ignored);
                insertChangelogAudit(changelogAuditVo, executeSqlParamVo.getNeatlogicConn());
                insertChangelogAuditDetail(sqlHash, executeSqlParamVo.getSql(), executeSqlParamVo.getNeatlogicConn());
                executeSqlParamVo.getErrStrWriter().getBuffer().setLength(0);
            } else {
                changelogAuditVo = new ChangelogAuditVo(tenantUuid, executeSqlParamVo.getModuleId(), sqlHash, executeSqlParamVo.getVersion(), 1);
                executeSqlParamVo.getChangelogSqlHashList().add(sqlHash);
                insertChangelogAudit(changelogAuditVo, executeSqlParamVo.getNeatlogicConn());
                insertChangelogAuditDetail(sqlHash, executeSqlParamVo.getSql(), executeSqlParamVo.getNeatlogicConn());
            }
        }
        return executeSqlParamVo.isError();
    }

    /**
     * 记录租户changelog记录
     *
     * @param changelogAuditVo changelog记录
     */
    private static void insertChangelogAudit(ChangelogAuditVo changelogAuditVo, Connection neatlogicConn) throws Exception {
        try (PreparedStatement statement = neatlogicConn.prepareStatement("insert into `changelog_audit` (`tenant_uuid`,`module_id`,`sql_hash`,`version`,`error_msg`,`sql_status`,`lcd`,`ignored`) VALUES (?,?,?,?,?,?,now(),?) ON DUPLICATE KEY UPDATE `error_msg` = ?,`sql_status` = ?, `lcd` = now(),`ignored` = ? ")) {
            statement.setString(1, changelogAuditVo.getTenantUuid());
            statement.setString(2, changelogAuditVo.getModuleId());
            statement.setString(3, changelogAuditVo.getSqlHash());
            statement.setString(4, changelogAuditVo.getVersion());
            statement.setString(5, changelogAuditVo.getErrorMsg());
            statement.setInt(6, changelogAuditVo.getSqlStatus());
            statement.setInt(7, changelogAuditVo.getIgnored());
            statement.setString(8, changelogAuditVo.getErrorMsg());
            statement.setInt(9, changelogAuditVo.getSqlStatus());
            statement.setInt(10, 1);//第二次启动自动忽略
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
    private static void insertChangelogAuditDetail(String hash, String sql, Connection neatlogicConn) throws Exception {
        try (PreparedStatement statement = neatlogicConn.prepareStatement("insert ignore into `changelog_audit_detail` (`hash`,`sql`) VALUES (?,?) ");) {
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
    private static void insertTenantModuleDmlSql(TenantModuleDmlSqlVo tenantModuleDmlSqlVo, Connection neatlogicConn) throws Exception {
        try (PreparedStatement statement = neatlogicConn.prepareStatement("insert into `tenant_module_dmlsql` (`tenant_uuid`,`module_id`,`sql_uuid`,`sql_status`,`error_msg`,`fcd`,`type`) VALUES (?,?,?,?,?,now(),?) ON DUPLICATE KEY UPDATE `sql_status` = ? , `error_msg` = ?")) {
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
    private static void insertTenantModuleDmlSqlDetail(String md5, String dmlSql, Connection neatlogicConn) throws Exception {
        try (PreparedStatement statement = neatlogicConn.prepareStatement("insert ignore into `tenant_module_dmlsql_detail` (`hash`,`sql`) VALUES (?,?) ")) {
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
