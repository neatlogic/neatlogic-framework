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

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Intercepts(
        @Signature(
                type = Executor.class,
                method = "query",
                args = {
                        MappedStatement.class,
                        Object.class,
                        RowBounds.class,
                        ResultHandler.class
                }
        )
)
public class LimitInterceptor implements Interceptor {
    private Field additionalParametersField;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        //获取拦截方法的参数
        Object[] args = invocation.getArgs();
        MappedStatement ms = (MappedStatement) args[0];
        Object parameterObject = args[1];
        RowBounds rowBounds = (RowBounds) args[2];
        ResultHandler resultHandler = (ResultHandler) args[3];
        //当前的目标对象
        Executor executor = (Executor) invocation.getTarget();
        /*
         * 判断sql语句是否有limit限制，如果没有就添加limit 10000
         * 1.limit ?
         * 2.limit 10000
         * 3.limit ?, ?
         * 4.limit 0, ?
         * 5.limit ?, 20
         * 5.limit 0, 20
         */
        boolean limited = true;
        BoundSql boundSql = ms.getBoundSql(parameterObject);
        String sql = boundSql.getSql();
        sql = sql.toLowerCase();
        String limit = "limit";
        int lastIndex = sql.lastIndexOf(limit);
        if (lastIndex != -1) {
            String afterLimit = sql.substring(lastIndex + limit.length());
            afterLimit = afterLimit.trim();
            String[] split = afterLimit.split(",", 2);
            for (String str : split) {
                str = str.trim();
                if (Objects.equals(str, "?")) {
                    continue;
                }
                if (StringUtils.isNumeric(str)) {
                    continue;
                }
                limited = false;
            }
        } else {
            limited = false;
        }
        //如果sql语句有limit限制，就不用增加limit 10000了
        if (limited) {
            return invocation.proceed();
        }
        //反射获取动态参数
        Map<String, Object> additionalParameters = new HashMap<>();
        try {
            additionalParametersField = BoundSql.class.getDeclaredField("additionalParameters");
            additionalParametersField.setAccessible(true);
            additionalParameters = (Map<String, Object>) additionalParametersField.get(boundSql);
        } catch (NoSuchFieldException ex) {
            throw new RuntimeException(ex);
        }

        // sql末尾增加 limit 10000
        CacheKey pageKey = executor.createCacheKey(ms, parameterObject, RowBounds.DEFAULT, boundSql);
        String pageSql = boundSql.getSql() + " limit 10000";
        BoundSql pageBoundSql = new BoundSql(ms.getConfiguration(), pageSql, boundSql.getParameterMappings(), parameterObject);
        for (String key : additionalParameters.keySet()) {
            pageBoundSql.setAdditionalParameter(key, additionalParameters.get(key));
        }
        return executor.query(ms, parameterObject, RowBounds.DEFAULT, resultHandler, pageKey, pageBoundSql);
    }

}
