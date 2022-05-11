/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.dao.util;

import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

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
        for (String selectId : selectIdList) {
            List reportTypeList = sqlSession.selectList(selectId, paramMap);
            resultMap.put(selectId, reportTypeList);
        }
        return resultMap;
    }

    /**
     * 执行mapper中特定某个select语句
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
        return sqlSession.selectList(id, paramMap);
    }

    /**
     * 获取mapper中所有select语句的id列表
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
     * @return
     */
    public List<Map<String, String>> getAllResultMappingList() {
        List<Map<String, String>> list = new ArrayList<>();
        Collection<ResultMap> resultMaps = configuration.getResultMaps();
        for (ResultMap resultMap : resultMaps) {
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
     * @return
     */
    public List getResultMapIdList() {
        List<String> idList = new ArrayList<>();
        Collection<ResultMap> resultMaps = configuration.getResultMaps();
        for (ResultMap resultMap : resultMaps) {
            idList.add(resultMap.getId());
        }
        return idList;
    }
}
