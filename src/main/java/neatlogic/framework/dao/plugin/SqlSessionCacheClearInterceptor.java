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

package neatlogic.framework.dao.plugin;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.sql.Connection;
import java.util.Map;

@Intercepts({
        @Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})
})
public class SqlSessionCacheClearInterceptor implements Interceptor {
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        // 执行完上面的任务后，不改变原有的sql执行过程
        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
        BoundSql boundSql = statementHandler.getBoundSql();
        String sql = boundSql.getSql();
        // 判断sql语句是否以“for update”结尾
        boolean isForUpdate = false;
        int index = 0;
        char[] lowerChars = {'e', 't', 'a', 'd', 'p', 'u', 'r', 'o','f'};
        char[] upperChars = {'E', 'T', 'A', 'D', 'P', 'U', 'R', 'O','F'};
        int length = sql.length();
        for (int i = length - 1; i >= 0; i--) {
            char c = sql.charAt(i);
            if (c >= 'A' && c <= 'Z') {
                if (upperChars[index] == c) {
                    index++;
                } else {
                    break;
                }
            } else if (c >= 'a' && c <= 'z') {
                if (lowerChars[index] == c) {
                    index++;
                } else {
                    break;
                }
            }
            if (index >= lowerChars.length) {
                isForUpdate = true;
                break;
            }
        }
        // 如果sql是以“for update”结尾，就清空当前SqlSession缓存
        if (isForUpdate) {
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
        return invocation.proceed();
    }
}
