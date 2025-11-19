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

import neatlogic.framework.common.config.Config;
import neatlogic.framework.store.mysql.SQLTransientConnectionExceptionAudit;
import neatlogic.framework.util.SnowflakeUtil;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.sql.Statement;
import java.util.Objects;

@Intercepts({
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
        @Signature(type = StatementHandler.class, method = "batch", args = {Statement.class}),
        @Signature(type = StatementHandler.class, method = "update", args = {Statement.class}),
        @Signature(type = StatementHandler.class, method = "query", args = {Statement.class, ResultHandler.class}),
        @Signature(type = StatementHandler.class, method = "queryCursor", args = {Statement.class}),
})
public class ExceptionAuditInterceptor implements Interceptor {

    private static final ThreadLocal<MappedStatement> mappedStatementThreadLocal = new ThreadLocal<>();

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        if (Objects.equals(Config.DATASOURCE_EXCEPTION_AUDIT(), 1)) {
            Object target = invocation.getTarget();
            if (target instanceof Executor) {
                try {
                    mappedStatementThreadLocal.set((MappedStatement) invocation.getArgs()[0]);
                    return invocation.proceed();
                } finally {
                    mappedStatementThreadLocal.remove();
                }
            } else {
                String key = Thread.currentThread().getName() + "#" + SnowflakeUtil.uniqueLong();
                try {
                    MappedStatement mappedStatement = mappedStatementThreadLocal.get();
                    String sqlId = mappedStatement.getId();
                    SQLTransientConnectionExceptionAudit.putExecutingSQL(key, sqlId);
                    return invocation.proceed();
                } finally {
                    SQLTransientConnectionExceptionAudit.removeExecutingSQL(key);
                }
            }
        } else {
            return invocation.proceed();
        }
    }
}
