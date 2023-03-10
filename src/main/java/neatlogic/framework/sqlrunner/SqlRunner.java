/*
Copyright(c) $today.year NeatLogic Co., Ltd. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package neatlogic.framework.sqlrunner;

import neatlogic.framework.dao.plugin.LimitInterceptor;
import neatlogic.framework.dao.plugin.PageInterceptor;
import neatlogic.framework.dao.plugin.PageRowBounds;
import neatlogic.framework.dao.plugin.SqlCostInterceptor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.session.*;
import org.mybatis.spring.transaction.SpringManagedTransactionFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class SqlRunner {
    private final static String DOCTYPE = "<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">";

    private static DataSource dataSource;

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

    public SqlRunner(String mapperXml, DataSource dataSource) {
        this(mapperXml, null, dataSource);
    }

    public SqlRunner(String mapperXml, String namespace, DataSource dataSource) {
        if (StringUtils.isNotBlank(namespace)) {
            this.namespace = namespace;
        }
        if (dataSource != null) {
            this.dataSource = dataSource;
        }

        Configuration configuration = new Configuration();
        configuration.addInterceptor(new SqlCostInterceptor());
        configuration.addInterceptor(new LimitInterceptor());
        configuration.addInterceptor(new PageInterceptor());
        Environment environment = new Environment("", new SpringManagedTransactionFactory(), this.dataSource);
        configuration.setEnvironment(environment);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(DOCTYPE);
        stringBuilder.append("<mapper namespace=\"" + this.namespace + "\">");

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

    @Resource
    public void setDataSource(DataSource _dataSource) {
        dataSource = _dataSource;
    }

    /**
     * ??????mapper???????????????select??????
     *
     * @param sqlInfo
     * @param paramMap
     * @return
     */
    public <E> List<E>  runSqlById(SqlInfo sqlInfo, Map<String, Object> paramMap) {
        MappedStatement mappedStatement = configuration.getMappedStatement(sqlInfo.getNamespace() + "." + sqlInfo.getId());
        if (mappedStatement == null) {
            return new ArrayList<>();
        }
        if (mappedStatement.getSqlCommandType() != SqlCommandType.SELECT) {
            return new ArrayList<>();
        }
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            return sqlSession.selectList(sqlInfo.getNamespace() + "." + sqlInfo.getId(), paramMap);
        } finally {
            sqlSession.close();
        }
    }

    /**
     * ??????mapper???????????????select??????
     *
     * @param sqlInfo
     * @param paramMap
     * @return
     */
    public <E> List<E>  runSqlById(SqlInfo sqlInfo, Map<String, Object> paramMap, PageRowBounds rowBounds) {
        MappedStatement mappedStatement = configuration.getMappedStatement(sqlInfo.getNamespace() + "." + sqlInfo.getId());
        if (mappedStatement == null) {
            return new ArrayList<>();
        }
        if (mappedStatement.getSqlCommandType() != SqlCommandType.SELECT) {
            return new ArrayList<>();
        }
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            return sqlSession.selectList(sqlInfo.getNamespace() + "." + sqlInfo.getId(), paramMap, rowBounds);
        } finally {
            sqlSession.close();
        }
    }

    /**
     * ??????mapper?????????select?????????????????????
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
            List<ParameterMapping> parameterMappingList = boundSql.getParameterMappings();
            sqlInfo.setParameterList(parameterMappingList.stream().map(ParameterMapping::getProperty).collect(Collectors.toList()));
            List<ResultMap> resultMaps = mappedStatement.getResultMaps();
            if (CollectionUtils.isNotEmpty(resultMaps)) {
                ResultMap resultMap = resultMaps.get(0);
                sqlInfo.setResultMap(resultMap.getId());
                sqlInfo.setResultType(resultMap.getType().getName());
                sqlInfo.setColumnList(new ArrayList<>(resultMap.getMappedColumns()));
                sqlInfo.setPropertyList(new ArrayList<>(resultMap.getMappedProperties()));
            }
            Integer timeout = mappedStatement.getTimeout();
            sqlInfo.setTimeout(timeout);
            sqlInfoList.add(sqlInfo);
        }
        return sqlInfoList;
    }

}
