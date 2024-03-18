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

package neatlogic.framework.dao.util;

import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.Map;

public class CacheUtil {
    /**
     * 清空当前事务会话的一级缓存
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
