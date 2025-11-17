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

package neatlogic.framework.dao.plugin;

import neatlogic.framework.asynchronization.threadlocal.InterceptorContext;
import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;

import java.lang.reflect.Field;
import java.sql.Connection;

/**
 * BG方式查询拦截器
 */
@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})})
public class DataSchemaInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        InterceptorContext interceptorContext = InterceptorContext.get();
        if (interceptorContext != null) {
            interceptorContext.setQueryFromDatabase(true);
        }
        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();

        BoundSql boundSql = statementHandler.getBoundSql();
        // 获取到原始sql语句
        String sql = boundSql.getSql();
        if (sql.contains("@{DATA_SCHEMA}")) {
            sql = sql.replace("@{DATA_SCHEMA}", TenantContext.get().getDataDbName());
            Field field = boundSql.getClass().getDeclaredField("sql");
            field.setAccessible(true);
            field.set(boundSql, sql);
        }
        return invocation.proceed();
    }

}
