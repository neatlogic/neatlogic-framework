/*Copyright (C) $today.year  深圳极向量科技有限公司 All Rights Reserved.

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

package neatlogic.framework.sqlrunner;

import java.util.List;

public class SqlInfo {
    /**
     * 唯一标识
     */
    private String id;
    /**
     * 预处理sql语句
     */
    private String sql;
    /**
     * 超时时间
     */
    private Integer timeout;
    /**
     * 返回结果类型
     */
    private String resultType;
    /**
     * 返回结果resultMap的id
     */
    private String resultMap;
    /**
     * resultMap中column列表
     */
    private List<String> columnList;
    /**
     * resultMap中property列表
     */
    private List<String> propertyList;
    /**
     * mapper的命名空间
     */
    private String namespace;
    /**
     * sql语句中参数列表
     */
    private List<String> parameterList;
    /**
     * 是否需要分页
     */
    private Boolean needPage = false;
    /**
     * 每页条数
     */
    private Integer pageSize = 20;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    public String getResultMap() {
        return resultMap;
    }

    public void setResultMap(String resultMap) {
        this.resultMap = resultMap;
    }

    public List<String> getColumnList() {
        return columnList;
    }

    public void setColumnList(List<String> columnList) {
        this.columnList = columnList;
    }

    public List<String> getPropertyList() {
        return propertyList;
    }

    public void setPropertyList(List<String> propertyList) {
        this.propertyList = propertyList;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public List<String> getParameterList() {
        return parameterList;
    }

    public void setParameterList(List<String> parameterList) {
        this.parameterList = parameterList;
    }

    public Boolean getNeedPage() {
        return needPage;
    }

    public void setNeedPage(boolean needPage) {
        this.needPage = needPage;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
