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

import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.dao.mapper.TenantMapper;
import neatlogic.framework.dto.TenantVo;
import neatlogic.framework.store.mysql.DatasourceManager;
import neatlogic.framework.store.mysql.NeatLogicBasicDataSource;
import neatlogic.framework.util.I18nUtils;
import neatlogic.framework.util.JdbcUtil;
import neatlogic.framework.util.Md5Util;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class ScriptRunnerManager {
    static Logger logger = LoggerFactory.getLogger(ScriptRunnerManager.class);

    private static TenantMapper tenantMapper;

    @Autowired
    public void setTenantMapper(TenantMapper _tenantMapper) {
        tenantMapper = _tenantMapper;
    }

    /**
     * 执行sql
     *
     * @param tenant       租户
     * @param moduleId     模块id
     * @param scriptReader 脚本读取
     * @param logWriter    日志
     * @param errWriter    错误日志
     */
    public static void runScript(TenantVo tenant, String moduleId, Reader scriptReader, PrintWriter logWriter, PrintWriter errWriter, boolean isDataDb) {
        Connection conn = null;
        String tenantUuid = tenant.getUuid();
        if (isDataDb) {
            tenantUuid = tenantUuid + "_data";
        }
        NeatLogicBasicDataSource tenantDatasource = DatasourceManager.getDatasource(tenantUuid);
        try {
            conn = tenantDatasource.getConnection();
            ScriptRunner runner = new ScriptRunner(conn);
            runner.setSendFullScript(false);
            runner.setAutoCommit(true);
            // 有错误会继续执行
            runner.setStopOnError(false);
            // Resources.setCharset(Charset.forName("UTF-8"));
            runner.setLogWriter(logWriter);
            runner.setErrorLogWriter(errWriter);
            runner.setDelimiter(";");
            runner.runScript(scriptReader);
            runner.closeConnection();
        } catch (SQLException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
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
     *
     * @param tenant       租户
     * @param moduleId     模块id
     * @param scriptReader 脚本读取
     * @param logWriter    日志
     * @param errWriter    错误日志
     */
    public static void runScriptOnce(TenantVo tenant, String moduleId, Reader scriptReader, PrintWriter logWriter, PrintWriter errWriter) {
        runScriptOnce(tenant, moduleId, scriptReader, logWriter, errWriter, false);
    }

    /**
     * 仅执行一次sql，执行过的sql跳过不执行
     *
     * @param tenant       租户
     * @param moduleId     模块id
     * @param scriptReader 脚本读取
     * @param logWriter    日志
     * @param errWriter    错误日志
     * @param isDataDb     是否data库
     */
    public static void runScriptOnce(TenantVo tenant, String moduleId, Reader scriptReader, PrintWriter logWriter, PrintWriter errWriter, Boolean isDataDb) {
        TenantContext.get().setUseDefaultDatasource(true);
        List<String> hasRunSqlMd5List = tenantMapper.getTenantModuleDmlSqlMd5ByTenantUuidAndModuleId(tenant.getUuid(), moduleId);
        List<String> currentRunSqlMd5List = new ArrayList<>();
        Connection conn = null;
        String tenantUuid = tenant.getUuid();
        if (isDataDb) {
            tenantUuid = tenantUuid + "_data";
        }
        NeatLogicBasicDataSource tenantDatasource = DatasourceManager.getDatasource(tenantUuid);
        try {
            BufferedReader scriptBufferedReader = new BufferedReader(scriptReader);
            conn = tenantDatasource.getConnection();
            ScriptRunner runner = new ScriptRunner(conn);
            runner.setLogWriter(logWriter);
            runner.setErrorLogWriter(errWriter);
            runner.setSendFullScript(false);
            runner.setAutoCommit(true);
            String line;
            while ((line = scriptBufferedReader.readLine()) != null) {
                if (StringUtils.isBlank(line.trim())) {
                    continue;
                }
                // 如果没有执行过改sql，则执行该 SQL 语句
                String sqlMd5 = Md5Util.encryptMD5(line);
                if (!hasRunSqlMd5List.contains(sqlMd5)) {
                    runner.runScript(new StringReader(line));
                    currentRunSqlMd5List.add(sqlMd5);
                    hasRunSqlMd5List.add(sqlMd5);
                }
            }
            runner.closeConnection();
            if (CollectionUtils.isNotEmpty(currentRunSqlMd5List)) {
                List<String> hasRunSqlMd5ListTmp = new ArrayList<>();
                for (int i = 0; i < currentRunSqlMd5List.size(); i++) {
                    if (i != 0 && i % 200 == 0) {
                        tenantMapper.insertTenantModuleDmlSql(tenant.getUuid(), moduleId, hasRunSqlMd5ListTmp, 1);
                        hasRunSqlMd5ListTmp.clear();
                    } else {
                        hasRunSqlMd5ListTmp.add(currentRunSqlMd5List.get(i));
                    }
                }
                if (CollectionUtils.isNotEmpty(hasRunSqlMd5ListTmp)) {
                    tenantMapper.insertTenantModuleDmlSql(tenant.getUuid(), moduleId, hasRunSqlMd5ListTmp, 1);
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        } catch (IOException e) {
            e.printStackTrace();
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
     *
     * @param tenant       租户
     * @param moduleId     模块id
     * @param scriptReader 脚本读取
     * @param isDataDb     是否data库
     */
    public static void runScriptOnceWithJdbc(TenantVo tenant, String moduleId, Reader scriptReader, Boolean isDataDb) {
        StringWriter logStrWriter = new StringWriter();
        PrintWriter logWriter = new PrintWriter(logStrWriter);
        StringWriter errStrWriter = new StringWriter();
        PrintWriter errWriter = new PrintWriter(errStrWriter);
        PreparedStatement sqlMd5Statement = null;
        ResultSet sqlMd5ResultSet = null;
        Connection conn = null;
        Connection neatlogicConn = null;
        ScriptRunner runner = null;
        try {
            List<String> hasRunSqlMd5List = new ArrayList<>();
            DataSource neatlogicDatasource = JdbcUtil.getNeatlogicDataSource();
            neatlogicConn = neatlogicDatasource.getConnection();
            sqlMd5Statement = neatlogicConn.prepareStatement("select sql_uuid from tenant_module_dmlsql where tenant_uuid = ? and `module_id` = ?");
            sqlMd5Statement.setString(1, tenant.getUuid());
            sqlMd5Statement.setString(2, moduleId);
            sqlMd5ResultSet = sqlMd5Statement.executeQuery();
            while (sqlMd5ResultSet.next()) {
                hasRunSqlMd5List.add(sqlMd5ResultSet.getString("sql_uuid"));
            }
            BufferedReader scriptBufferedReader = new BufferedReader(scriptReader);
            try {
                conn = JdbcUtil.getNeatlogicDataSource(tenant, isDataDb).getConnection();
            } catch (Exception exception) {
                System.out.println("租户:" + tenant.getName() + "[" + tenant.getName() + "] 连接不上,请确认neatlogic库的datasource该租户的数据是否正确后重启");
                System.exit(1);
            }
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
                    insertTenantModuleDmlSql(tenant.getUuid(), moduleId, sqlMd5, neatlogicConn);
                    if (StringUtils.isNotBlank(errStrWriter.toString())) {
                        logger.error(String.format(I18nUtils.getStaticMessage("nfes.dmlsqlexecuteexception.dmlsqlexecuteexception"), tenant.getName(), moduleId, line));
                        System.exit(1);
                    }
                    hasRunSqlMd5List.add(sqlMd5);
                }
            }
        } catch (Exception ex) {
            logger.error(String.format("租户%s初始化dml失败", tenant.getName()));
            logger.error(ex.getMessage(), ex);
        } finally {
            if (runner != null) {
                runner.closeConnection();
            }
            JdbcUtil.closeConnection(conn);
            JdbcUtil.closeConnection(neatlogicConn);
            JdbcUtil.closeResultSet(sqlMd5ResultSet);
            JdbcUtil.closeStatement(sqlMd5Statement);
        }
    }

    private static void insertTenantModuleDmlSql(String tenantUuid, String moduleId, String md5, Connection neatlogicConn) throws SQLException {
        String sql = "insert into `tenant_module_dmlsql` (`tenant_uuid`,`module_id`,`sql_uuid`,`sql_status`,`fcd`) VALUES (?,?,?,?,now())";
        PreparedStatement statement = neatlogicConn.prepareStatement(sql);
        statement.setString(1, tenantUuid);
        statement.setString(2, moduleId);
        statement.setString(3, md5);
        statement.setInt(4, 1);
        statement.execute();
    }
}
