/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.dao.util;

import codedriver.framework.dao.plugin.SqlCostInterceptor;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.mybatis.spring.transaction.SpringManagedTransactionFactory;

import javax.sql.DataSource;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class SqlUtil {

    private Configuration configuration;
    private SqlSessionFactory sqlSessionFactory;

    public SqlUtil(Configuration configuration) {
        this.configuration = configuration;
        this.sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
    }

    /**
     * 执行mapper中所有select语句
     *
     * @param paramMap
     * @return
     */
    public Map<String, List> executeAllSelectMappedStatement(Map<String, Object> paramMap) {
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
    public List executeAllSelectMappedStatementById(String id, Map<String, Object> paramMap) {
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
     * 获取mapper中所有select语句的id列表
     *
     * @return
     */
    public List<String> getAllSelectMappedStatementIdList() {
        List<String> selectIdList = new ArrayList<>();
        Collection<MappedStatement> mappedStatementList = configuration.getMappedStatements();
        for (MappedStatement mappedStatement : mappedStatementList) {
            SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();
            if (sqlCommandType == SqlCommandType.SELECT) {
                String selectId = mappedStatement.getId();
                if (!selectIdList.contains(selectId)) {
                    selectIdList.add(selectId);
                }
            }
        }
        return selectIdList;
    }

    /**
     * 获取mapper中所有resultMap的column与property的映射关系
     *
     * @return
     */
    public List<Map<String, String>> getAllResultMappingList() {
        List<Map<String, String>> list = new ArrayList<>();
        Set<String> idSet = new HashSet<>();
        Collection<ResultMap> resultMaps = configuration.getResultMaps();
        for (ResultMap resultMap : resultMaps) {
            if (idSet.contains(resultMap.getId())) {
                continue;
            }
            idSet.add(resultMap.getId());
            List<ResultMapping> resultMappings = resultMap.getResultMappings();
            for (ResultMapping resultMapping : resultMappings) {
                Map<String, String> map = new HashMap<>();
                map.put("column", resultMapping.getColumn());
                map.put("property", resultMapping.getProperty());
                list.add(map);
            }
        }
        return list;
    }

    /**
     * 获取mapper中特定某个resultMap的column与property的映射关系
     *
     * @return
     */
    public List<Map<String, String>> getResultMappingListByResultMapId(String id) {
        List<Map<String, String>> list = new ArrayList<>();
        ResultMap resultMap = configuration.getResultMap(id);
        if (resultMap == null) {
            return list;
        }
        List<ResultMapping> resultMappings = resultMap.getResultMappings();
        for (ResultMapping resultMapping : resultMappings) {
            Map<String, String> map = new HashMap<>();
            map.put("column", resultMapping.getColumn());
            map.put("property", resultMapping.getProperty());
            list.add(map);
        }
        return list;
    }

    /**
     * 获取mapper中所有resultMap的id列表
     *
     * @return
     */
    public List getResultMapIdList() {
        List<String> idList = new ArrayList<>();
        Collection<ResultMap> resultMaps = configuration.getResultMaps();
        for (ResultMap resultMap : resultMaps) {
            if (!idList.contains(resultMap.getId())) {
                idList.add(resultMap.getId());
            }
        }
        return idList;
    }

    public static class SqlUtilBuilder {

        private final static String DOCTYPE = "<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">";
        private DataSource dataSource;
        private String namespace;

        public SqlUtilBuilder(DataSource dataSource) {
            this.dataSource = dataSource;
        }

        public SqlUtilBuilder withNamespace(String namespace) {
            this.namespace = namespace;
            return this;
        }

        public SqlUtil build(String mapperXml) throws Exception {
            Configuration configuration = new Configuration();
            configuration.addInterceptor(new SqlCostInterceptor());
            Environment environment = new Environment("", new SpringManagedTransactionFactory(), dataSource);
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
                XMLMapperBuilder mapperParser = new XMLMapperBuilder(inputStream, configuration, "", configuration.getSqlFragments());
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
                        inputStream.close();
                    }
                }
            }
            return new SqlUtil(configuration);
        }
    }
}
