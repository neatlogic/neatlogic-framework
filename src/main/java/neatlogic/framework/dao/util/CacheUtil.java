/*
Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.

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
