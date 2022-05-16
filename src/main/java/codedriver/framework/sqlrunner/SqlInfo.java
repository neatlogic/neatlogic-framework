/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.sqlrunner;

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
}
