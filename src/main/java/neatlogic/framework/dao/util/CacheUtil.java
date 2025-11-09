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
