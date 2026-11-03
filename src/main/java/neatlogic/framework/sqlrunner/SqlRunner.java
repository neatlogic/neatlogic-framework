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

package neatlogic.framework.sqlrunner;

import neatlogic.framework.common.config.Config;
import neatlogic.framework.dao.plugin.*;
import neatlogic.framework.exception.sql.SqlRunnerAccessDeniedException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.mybatis.spring.transaction.SpringManagedTransactionFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class SqlRunner {
    private static final String DOCTYPE = "<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">";

    // Spring 注入的共享路由源仅作为默认值，自定义源只属于当前执行器实例。
    private static DataSource defaultDataSource;

    private DataSource dataSource;

    private String namespace = "neatlogic";

    private Configuration configuration;
    private SqlSessionFactory sqlSessionFactory;

    public SqlRunner() {

    }

    public SqlRunner(String mapperXml) {
        this(mapperXml, null, null);
    }

    public SqlRunner(String mapperXml, String namespace) {
        this(mapperXml, namespace, null);
    }

    public SqlRunner(String mapperXml, DataSource _dataSource) {
        this(mapperXml, null, _dataSource);
    }

    /** 构建独立的报表 SQL 会话配置，所有数据源均受当前租户访问策略约束。 */
    public SqlRunner(String mapperXml, String namespace, DataSource _dataSource) {
        if (StringUtils.isNotBlank(namespace)) {
            this.namespace = namespace;
        }
        if (_dataSource != null) {
            dataSource = _dataSource;
        } else {
            dataSource = defaultDataSource;
        }

        Configuration configuration = new Configuration();
        configuration.addInterceptor(new DataSchemaInterceptor());
        configuration.addInterceptor(new SqlRunnerAccessInterceptor());
        configuration.addInterceptor(new SqlCostInterceptor());
        configuration.addInterceptor(new LimitInterceptor());
        configuration.addInterceptor(new PageInterceptor());
        Environment environment = new Environment("", new SpringManagedTransactionFactory(), dataSource);
        configuration.setEnvironment(environment);
        configuration.setDefaultStatementTimeout(Config.SQLRUNNER_QUERY_TIMEOUT());
        configuration.setCallSettersOnNulls(true);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(DOCTYPE);
        stringBuilder.append("<mapper namespace=\"").append(this.namespace).append("\">");

        stringBuilder.append(mapperXml.substring("<mapper>".length()));
        ByteArrayInputStream inputStream = null;
        Throwable var8 = null;
        try {
            inputStream = new ByteArrayInputStream(stringBuilder.toString().getBytes(StandardCharsets.UTF_8));
            XMLMapperBuilder mapperParser = new XMLMapperBuilder(inputStream, configuration, this.namespace, configuration.getSqlFragments());
            mapperParser.parse();
        } catch (Throwable var32) {
            var8 = var32;
            throw var32;
        } finally {
            if (inputStream != null) {
                if (var8 != null) {
                    try {
                        inputStream.close();
                    } catch (Throwable var30) {
                        var8.addSuppressed(var30);
                    }
                } else {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        this.configuration = configuration;
        this.sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
    }

    /** 保存框架默认路由源，不覆盖已经构建的执行器实例。 */
    @Resource
    public void setDataSource(DataSource _dataSource) {
        defaultDataSource = _dataSource;
    }

    /**
     * 执行 mapper 中指定查询，所有动态 SQL 均受租户库隔离约束。
     *
     * @param sqlInfo
     * @param paramMap
     * @return
     */
    public <E> List<E>  runSqlById(SqlInfo sqlInfo, Map<String, Object> paramMap) {
        SqlRunnerAccessPolicy.validateTenant();
        MappedStatement mappedStatement = configuration.getMappedStatement(sqlInfo.getNamespace() + "." + sqlInfo.getId());
        if (mappedStatement == null) {
            return new ArrayList<>();
        }
        if (mappedStatement.getSqlCommandType() != SqlCommandType.SELECT) {
            throw new SqlRunnerAccessDeniedException();
        }
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            return sqlSession.selectList(sqlInfo.getNamespace() + "." + sqlInfo.getId(), paramMap);
        } catch (RuntimeException ex) {
            throw sanitizeAccessException(ex);
        } finally {
            sqlSession.close();
        }
    }

    /**
     * 执行 mapper 中指定分页查询，计数和数据查询均受租户库隔离约束。
     *
     * @param sqlInfo
     * @param paramMap
     * @return
     */
    public <E> List<E>  runSqlById(SqlInfo sqlInfo, Map<String, Object> paramMap, PageRowBounds rowBounds) {
        SqlRunnerAccessPolicy.validateTenant();
        MappedStatement mappedStatement = configuration.getMappedStatement(sqlInfo.getNamespace() + "." + sqlInfo.getId());
        if (mappedStatement == null) {
            return new ArrayList<>();
        }
        if (mappedStatement.getSqlCommandType() != SqlCommandType.SELECT) {
            throw new SqlRunnerAccessDeniedException();
        }
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            return sqlSession.selectList(sqlInfo.getNamespace() + "." + sqlInfo.getId(), paramMap, rowBounds);
        } catch (RuntimeException ex) {
            throw sanitizeAccessException(ex);
        } finally {
            sqlSession.close();
        }
    }

    /** 去掉 MyBatis 包装异常中的完整 SQL，安全拒绝只返回统一错误。 */
    private RuntimeException sanitizeAccessException(RuntimeException exception) {
        Throwable cause = exception;
        java.util.Set<Throwable> visited = java.util.Collections.newSetFromMap(new java.util.IdentityHashMap<>());
        while (cause != null && visited.add(cause)) {
            if (cause instanceof SqlRunnerAccessDeniedException) {
                return new SqlRunnerAccessDeniedException();
            }
            cause = cause.getCause();
        }
        return exception;
    }

    /**
     * 获取mapper中所有select语句的信息列表
     *
     * @return
     */
    public List<SqlInfo> getAllSqlInfoList(Map<String, Object> paramMap) {
        List<SqlInfo> sqlInfoList = new ArrayList<>();
        List<String> idList = new ArrayList<>();
        Collection<MappedStatement> mappedStatementList = configuration.getMappedStatements();
        for (MappedStatement mappedStatement : mappedStatementList) {
            SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();
            if (sqlCommandType != SqlCommandType.SELECT) {
                continue;
            }
            String id = mappedStatement.getId();
            if (idList.contains(id)) {
                continue;
            }
            idList.add(id);
            String namespace = mappedStatement.getResource();
            SqlInfo sqlInfo = new SqlInfo();
            sqlInfo.setId(id.substring(namespace.length() + 1));
            sqlInfo.setNamespace(namespace);
            BoundSql boundSql = mappedStatement.getBoundSql(paramMap);
            String sql = boundSql.getSql();
            sqlInfo.setSql(sql);
            sqlInfo.setExecutableSql(SqlCostInterceptor.getSql(mappedStatement, paramMap));
            List<ParameterMapping> parameterMappingList = boundSql.getParameterMappings();
            sqlInfo.setParameterList(parameterMappingList.stream().map(ParameterMapping::getProperty).collect(Collectors.toList()));
            List<ResultMap> resultMaps = mappedStatement.getResultMaps();
            if (CollectionUtils.isNotEmpty(resultMaps)) {
                ResultMap resultMap = resultMaps.get(0);
                sqlInfo.setResultMap(resultMap.getId());
                sqlInfo.setResultType(resultMap.getType().getName());
                List<String> columnList = new ArrayList<>();
                List<String> propertyList = new ArrayList<>();
                List<ResultMapping> resultMappings = resultMap.getResultMappings();
                if (CollectionUtils.isNotEmpty(resultMappings)) {
                    for (ResultMapping resultMapping : resultMappings) {
                        columnList.add(resultMapping.getColumn());
                        propertyList.add(resultMapping.getProperty());
                    }
                }
                sqlInfo.setColumnList(columnList);
                sqlInfo.setPropertyList(propertyList);
            }
            Integer timeout = mappedStatement.getTimeout();
            sqlInfo.setTimeout(timeout);
            sqlInfoList.add(sqlInfo);
        }
        return sqlInfoList;
    }

}
