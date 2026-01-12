/*
 *
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the NeatLogic Sustainable Use License (NSUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */

package neatlogic.framework.dto;

import neatlogic.framework.changelog.ConnectionHolder;
import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.restful.annotation.EntityField;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.springframework.core.io.Resource;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ExecuteSqlParamVo {
    @EntityField(name = "sql", type = ApiParamType.STRING)
    String sql;
    @EntityField(name = "执行过的sql md5列表", type = ApiParamType.STRING)
    List<String> changelogSqlHashList;
    @EntityField(name = "执行过的dml sql md5列表", type = ApiParamType.STRING)
    List<String> dmlSqlHashList;
    @EntityField(name = "sql执行对象", type = ApiParamType.STRING)
    ScriptRunner runner;
    @EntityField(name = "异常输出writer", type = ApiParamType.STRING)
    StringWriter errStrWriter;
    @EntityField(name = "异常输出writer", type = ApiParamType.STRING)
    PrintWriter errWriter;
    @EntityField(name = "log输出writer", type = ApiParamType.STRING)
    PrintWriter logWriter;
    @EntityField(name = "log输出writer", type = ApiParamType.STRING)
    StringWriter logStrWriter;
    @EntityField(name = "租户", type = ApiParamType.STRING)
    TenantVo tenant;
    @EntityField(name = "模块id", type = ApiParamType.STRING)
    String moduleId;
    @EntityField(name = "版本", type = ApiParamType.STRING)
    String version;
    @EntityField(name = "sql文件名", type = ApiParamType.STRING)
    String sqlFile;
    @EntityField(name = "是否异常", type = ApiParamType.BOOLEAN)
    boolean isError = false;
    @EntityField(name = "sql 分隔符", type = ApiParamType.STRING)
    String delimiter = ";";
    @EntityField(name = "是否执行整个sql，无需分隔", type = ApiParamType.BOOLEAN)
    boolean isAll = false;
    @EntityField(name = "是否data库", type = ApiParamType.BOOLEAN)
    boolean idDataDB = false;
    @EntityField(name = "sql文件resource", type = ApiParamType.JSONOBJECT)
    Resource resource;
    @EntityField(name = "neatlogic库链接", type = ApiParamType.JSONOBJECT)
    ConnectionHolder neatlogicConnectHolder;
    @EntityField(name = "neatlogic租户库链接", type = ApiParamType.JSONOBJECT)
    ConnectionHolder neatlogicTenantConnectHolder;
    @EntityField(name = "是否需要新连接", type = ApiParamType.BOOLEAN)
    boolean isNeedNewRunner = false;

    public ExecuteSqlParamVo() {

    }

    public ExecuteSqlParamVo(TenantVo tenant, String moduleId, String version, Resource resource, List<String> changelogSqlHashList, ConnectionHolder neatlogicTenantConnectHolder, ConnectionHolder neatlogicConnectHolder) {
        this.changelogSqlHashList = changelogSqlHashList;
        this.tenant = tenant;
        this.moduleId = moduleId;
        this.version = version;
        this.resource = resource;
        this.neatlogicTenantConnectHolder = neatlogicTenantConnectHolder;
        this.neatlogicConnectHolder = neatlogicConnectHolder;
    }

    public ExecuteSqlParamVo(TenantVo tenant, String moduleId, Resource resource, List<String> dmlSqlHashList, ConnectionHolder neatlogicTenantConnectHolder, ConnectionHolder neatlogicConnectHolder) {
        this.dmlSqlHashList = dmlSqlHashList;
        this.tenant = tenant;
        this.moduleId = moduleId;
        this.resource = resource;
        this.neatlogicTenantConnectHolder = neatlogicTenantConnectHolder;
        this.neatlogicConnectHolder = neatlogicConnectHolder;
    }

    public ExecuteSqlParamVo(String moduleId, String version, Resource resource, ConnectionHolder neatlogicConnectHolder) {
        this.moduleId = moduleId;
        this.version = version;
        this.resource = resource;
        this.neatlogicConnectHolder = neatlogicConnectHolder;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public List<String> getChangelogSqlHashList() {
        return changelogSqlHashList;
    }

    public void setChangelogSqlHashList(List<String> changelogSqlHashList) {
        if(CollectionUtils.isNotEmpty(changelogSqlHashList)) {
            this.changelogSqlHashList = changelogSqlHashList;
        }else{
            this.changelogSqlHashList = new ArrayList<>();
        }
    }

    public ScriptRunner getRunner() {
        return runner;
    }

    public void setRunner(ScriptRunner runner) {
        this.runner = runner;
    }

    public StringWriter getErrStrWriter() {
        return errStrWriter;
    }

    public void setErrStrWriter(StringWriter errStrWriter) {
        this.errStrWriter = errStrWriter;
    }

    public TenantVo getTenant() {
        return tenant;
    }

    public void setTenant(TenantVo tenant) {
        this.tenant = tenant;
    }

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getSqlFile() {
        if (resource != null) {
            return resource.getFilename();
        }
        return sqlFile;
    }

    public void setSqlFile(String sqlFile) {
        this.sqlFile = sqlFile;
    }

    public boolean isError() {
        return isError;
    }

    public void setError(boolean error) {
        isError = error;
    }

    public PrintWriter getErrWriter() {
        return errWriter;
    }

    public void setErrWriter(PrintWriter errWriter) {
        this.errWriter = errWriter;
    }

    public String getDelimiter() {
        return delimiter;
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    public boolean isAll() {
        return isAll;
    }

    public void setAll(boolean all) {
        isAll = all;
    }

    public boolean isIdDataDB() {
        return idDataDB;
    }

    public void setIdDataDB(boolean idDataDB) {
        this.idDataDB = idDataDB;
    }

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public ConnectionHolder getNeatlogicConnectHolder() {
        return neatlogicConnectHolder;
    }

    public void setNeatlogicConnectHolder(ConnectionHolder neatlogicConnectHolder) {
        this.neatlogicConnectHolder = neatlogicConnectHolder;
    }

    public ConnectionHolder getNeatlogicTenantConnectHolder() {
        return neatlogicTenantConnectHolder;
    }

    public void setNeatlogicTenantConnectHolder(ConnectionHolder neatlogicTenantConnectHolder) {
        this.neatlogicTenantConnectHolder = neatlogicTenantConnectHolder;
    }

    public List<String> getDmlSqlHashList() {
        return dmlSqlHashList;
    }

    public void setDmlSqlHashList(List<String> dmlSqlHashList) {
        this.dmlSqlHashList = dmlSqlHashList;
    }

    public PrintWriter getLogWriter() {
        return logWriter;
    }

    public void setLogWriter(PrintWriter logWriter) {
        this.logWriter = logWriter;
    }

    public StringWriter getLogStrWriter() {
        return logStrWriter;
    }

    public void setLogStrWriter(StringWriter logStrWriter) {
        this.logStrWriter = logStrWriter;
    }

    public boolean isNeedNewRunner() {
        if(this.runner == null){
            return true;
        }
        return isNeedNewRunner;
    }

    public void setNeedNewRunner(boolean needNewRunner) {
        isNeedNewRunner = needNewRunner;
    }

    public void invalidate() throws SQLException {
        if (tenant == null) {
            neatlogicConnectHolder.invalidate();
        }else{
            neatlogicTenantConnectHolder.invalidate();
        }
        isNeedNewRunner = true;
    }
}
