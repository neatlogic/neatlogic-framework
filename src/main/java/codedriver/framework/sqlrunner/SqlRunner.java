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

    private String namespace;

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
        if (namespace != null) {
            this.namespace = namespace;
        }
        if (dataSource != null) {
            this.dataSource = dataSource;
        }

        Configuration configuration = new Configuration();
        configuration.addInterceptor(new SqlCostInterceptor());
        Environment environment = new Environment("", new SpringManagedTransactionFactory(), this.dataSource);
        configuration.setEnvironment(environment);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(DOCTYPE);
        if (StringUtils.isNotBlank(this.namespace)) {
            stringBuilder.append("<mapper namespace=\"" + this.namespace + "\">");
        } else {
            stringBuilder.append("<mapper namespace=\"codedriver\">");
        }

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
//    private SqlUtil(Configuration configuration) {
//        this.configuration = configuration;
//        this.sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
//    }

    /**
     * 执行mapper中所有select语句
     *
     * @param paramMap
     * @return
     */
    public Map<String, List> runAllSql(Map<String, Object> paramMap) {
        Map<String, List> resultMap = new HashMap<>();
        List<String> selectIdList = new ArrayList<>();
        Collection<MappedStatement> mappedStatementList = configuration.getMappedStatements();
        for (MappedStatement mappedStatement : mappedStatementList) {
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
                resultMap.put(selectId, reportTypeList);
            }
        } finally {
            sqlSession.close();
        }
        return resultMap;
    }

    /**
     * 执行mapper中特定某个select语句
     *
     * @param id
     * @param paramMap
     * @return
     */
    public List runSqlById(String id, Map<String, Object> paramMap) {
        List<String> selectIdList = new ArrayList<>();
        MappedStatement mappedStatement = configuration.getMappedStatement(id);
        if (mappedStatement == null) {
            return new ArrayList<>();
        }
        if (mappedStatement.getSqlCommandType() != SqlCommandType.SELECT) {
            return new ArrayList<>();
        }
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            return sqlSession.selectList(id, paramMap);
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
            if (sqlCommandType == SqlCommandType.SELECT) {
                continue;
            }
            String id = mappedStatement.getId();
            if (idList.contains(id)) {
                continue;
            }
            idList.add(id);
            SqlInfo sqlInfo = new SqlInfo();
            sqlInfo.setId(id);
            BoundSql boundSql = mappedStatement.getBoundSql(paramMap);
            String sql = boundSql.getSql();
            sqlInfo.setSql(sql);
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
