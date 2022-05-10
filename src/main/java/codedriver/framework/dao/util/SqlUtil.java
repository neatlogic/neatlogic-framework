/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.dao.util;

import org.apache.ibatis.mapping.MappedStatement;
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

    public Map<String, List> executeAllSelectMappedStatement(Map<String, Object> paramMap) {
        Map<String, List> resultMap = new HashMap<>();
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
        SqlSession sqlSession = sqlSessionFactory.openSession();
        for (String selectId : selectIdList) {
            System.out.println(selectId);
            List reportTypeList = sqlSession.selectList(selectId, paramMap);
            resultMap.put(selectId, reportTypeList);
        }
        return resultMap;
    }
    public List executeAllSelectMappedStatementById(String id, Map<String, Object> paramMap) {
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
        SqlSession sqlSession = sqlSessionFactory.openSession();
        for (String selectId : selectIdList) {
            System.out.println(selectId);
            if (Objects.equals(id, selectId)) {
                return sqlSession.selectList(selectId, paramMap);
            }
        }
        return null;
    }
}
