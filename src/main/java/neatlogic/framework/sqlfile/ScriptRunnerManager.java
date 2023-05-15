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
import neatlogic.framework.util.Md5Util;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.sql.Connection;
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

    public static void runScript(TenantVo tenant, String moduleId, Reader scriptReader, PrintWriter logWriter, PrintWriter errWriter) throws Exception {
        Connection conn = null;
        NeatLogicBasicDataSource tenantDatasource = DatasourceManager.getDatasource(tenant.getUuid());
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
        TenantContext.get().setUseDefaultDatasource(true);
        List<String> hasRunSqlMd5List = tenantMapper.getTenantModuleDmlSqlMd5ByTenantUuidAndModuleId(tenant.getUuid(), moduleId);
        List<String> currentRunSqlMd5List = new ArrayList<>();
        Connection conn = null;
        NeatLogicBasicDataSource tenantDatasource = DatasourceManager.getDatasource(tenant.getUuid());
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
            if(CollectionUtils.isNotEmpty(currentRunSqlMd5List)) {
                tenantMapper.insertTenantModuleDmlSql(tenant.getUuid(), moduleId, currentRunSqlMd5List, 1);
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
}
