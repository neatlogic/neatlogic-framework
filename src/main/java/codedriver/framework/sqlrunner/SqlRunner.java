/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.sqlrunner;

import codedriver.framework.dao.plugin.SqlCostInterceptor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.mybatis.spring.transaction.SpringManagedTransactionFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

//    private String namespace;

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
//        if (namespace != null) {
//            this.namespace = namespace;
//        }
        if (dataSource != null) {
            this.dataSource = dataSource;
        }

        Configuration configuration = new Configuration();
        configuration.addInterceptor(new SqlCostInterceptor());

        Environment environment = new Environment("", new SpringManagedTransactionFactory(), this.dataSource);
        configuration.setEnvironment(environment);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(DOCTYPE);
        if (StringUtils.isNotBlank(namespace)) {
            stringBuilder.append("<mapper namespace=\"" + namespace + "\">");
        } else {
            stringBuilder.append("<mapper namespace=\"codedriver\">");
        }

        stringBuilder.append(mapperXml.substring("<mapper>".length()));
        ByteArrayInputStream inputStream = null;
        Throwable var8 = null;
        try {
            inputStream = new ByteArrayInputStream(stringBuilder.toString().getBytes(StandardCharsets.UTF_8));
            XMLMapperBuilder mapperParser = new XMLMapperBuilder(inputStream, configuration, namespace, configuration.getSqlFragments());
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

//    @Autowired
//    public SqlRunner(DataSource dataSource) {
//        this.dataSource = dataSource;
//        this.configuration = new Configuration();
//        configuration.addInterceptor(new SqlCostInterceptor());
//        System.out.println("CacheEnabled:" + configuration.isCacheEnabled());
//        System.out.println("dataSource:" + dataSource);
//        Environment environment = new Environment("", new SpringManagedTransactionFactory(), this.dataSource);
//        configuration.setEnvironment(environment);
//        this.sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
//    }

//    public void addMapperXml(String mapperXml, String namespace) {
//        StringBuilder stringBuilder = new StringBuilder();
//        stringBuilder.append(DOCTYPE);
//        if (StringUtils.isNotBlank(namespace)) {
//            stringBuilder.append("<mapper namespace=\"" + namespace + "\">");
//        } else {
//            stringBuilder.append("<mapper namespace=\"codedriver\">");
//        }
//
//        stringBuilder.append(mapperXml.substring("<mapper>".length()));
//        ByteArrayInputStream inputStream = null;
//        Throwable var8 = null;
//        try {
//            inputStream = new ByteArrayInputStream(stringBuilder.toString().getBytes(StandardCharsets.UTF_8));
//            XMLMapperBuilder mapperParser = new XMLMapperBuilder(inputStream, configuration, namespace, configuration.getSqlFragments());
//            mapperParser.parse();
//        } catch (Throwable var32) {
//            var8 = var32;
//            throw var32;
//        } finally {
//            if (inputStream != null) {
//                if (var8 != null) {
//                    try {
//                        inputStream.close();
//                    } catch (Throwable var30) {
//                        var8.addSuppressed(var30);
//                    }
//                } else {
//                    try {
//                        inputStream.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }
//    }
    /**
     * 执行mapper中所有select语句
     *
     * @param paramMap
     * @return
     */
    public Map<String, List> runAllSql(String namespace, Map<String, Object> paramMap) {
        Map<String, List> resultMap = new HashMap<>();
        List<String> selectIdList = new ArrayList<>();
        Collection<MappedStatement> mappedStatementList = configuration.getMappedStatements();
        for (MappedStatement mappedStatement : mappedStatementList) {
            if (!Objects.equals(mappedStatement.getResource(), namespace)) {
                continue;
            }
            if (mappedStatement.getSqlCommandType() == SqlCommandType.SELECT) {
                String selectId = mappedStatement.getId();
                if (!selectIdList.contains(selectId)) {
                    selectIdList.add(selectId);
                }
            }
        }
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            for (String selectId : selectIdList) {
                List reportTypeList = sqlSession.selectList(selectId, paramMap);
                resultMap.put(selectId.substring(namespace.length() + 1), reportTypeList);
            }
        } finally {
            sqlSession.close();
        }
        return resultMap;
    }

    /**
     * 执行mapper中特定某个select语句
     *
     * @param sqlInfo
     * @param paramMap
     * @return
     */
    public <E> List<E>  runSqlById(SqlInfo sqlInfo, Map<String, Object> paramMap) {
        MappedStatement mappedStatement = configuration.getMappedStatement(sqlInfo.getId());
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
            sqlInfo.setNamespace(namespace);
            sqlInfoList.add(sqlInfo);
        }
        return sqlInfoList;
    }

}
