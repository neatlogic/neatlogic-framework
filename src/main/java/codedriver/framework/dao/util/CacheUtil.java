/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.dao.util;

import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.Map;

public class CacheUtil {
    /**
     * 清空当前事务会话的一级缓存，避免脏读
     */
    public static void clearSqlSessionCache(){
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            Map<Object, Object> resourceMap = TransactionSynchronizationManager.getResourceMap();
            for (Map.Entry<Object, Object> entry : resourceMap.entrySet()) {
                Object holder = entry.getValue();
                if (holder instanceof SqlSessionHolder) {
                    SqlSessionHolder sqlSessionHolder = (SqlSessionHolder) holder;
                    SqlSession sqlSession = sqlSessionHolder.getSqlSession();
                    if (sqlSession != null) {
                        sqlSession.clearCache();
                    }
                }
            }
        }
    }
}
