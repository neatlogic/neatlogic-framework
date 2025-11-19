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
import neatlogic.framework.common.config.Config;
import neatlogic.framework.store.mysql.SQLTransientConnectionExceptionAudit;
import neatlogic.framework.util.SnowflakeUtil;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;

import java.sql.Statement;

@Intercepts({
        @Signature(type = StatementHandler.class, method = "batch", args = {Statement.class}),
        @Signature(type = StatementHandler.class, method = "update", args = {Statement.class}),
        @Signature(type = StatementHandler.class, method = "query", args = {Statement.class, ResultHandler.class}),
        @Signature(type = StatementHandler.class, method = "queryCursor", args = {Statement.class}),
})
public class ExecutingSQLInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        if (Config.DATASOURCE_SQL_TRANSIENT_CONNECTION_EXCEPTION_AUDIT_ENABLE()) {
            String key = Thread.currentThread().getName() + "#" + SnowflakeUtil.uniqueLong();
            try {
                InterceptorContext interceptorContext = InterceptorContext.get();
                if (interceptorContext != null) {
                    MappedStatement mappedStatement = interceptorContext.getMappedStatement();
                    String sqlId = mappedStatement.getId();
                    SQLTransientConnectionExceptionAudit.putExecutingSQL(key, sqlId);
                }
                return invocation.proceed();
            } finally {
                SQLTransientConnectionExceptionAudit.removeExecutingSQL(key);
            }
        } else {
            return invocation.proceed();
        }
    }
}
