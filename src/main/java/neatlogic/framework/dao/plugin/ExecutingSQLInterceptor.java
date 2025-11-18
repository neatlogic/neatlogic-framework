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
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;

import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Intercepts({
        @Signature(type = StatementHandler.class, method = "batch", args = {Statement.class}),
        @Signature(type = StatementHandler.class, method = "update", args = {Statement.class}),
        @Signature(type = StatementHandler.class, method = "query", args = {Statement.class, ResultHandler.class}),
        @Signature(type = StatementHandler.class, method = "queryCursor", args = {Statement.class}),
})
public class ExecutingSQLInterceptor implements Interceptor {

    private final static Map<String, String> thread2ExecutingSQLMap = new ConcurrentHashMap<>();

    public static Map<String, String> getThread2ExecutingSQLMap() {
        return new HashMap<>(thread2ExecutingSQLMap);
    }

    public static void clearThread2ExecutingSQLMap() {
        thread2ExecutingSQLMap.clear();
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        try {
            InterceptorContext interceptorContext = InterceptorContext.get();
            if (interceptorContext != null) {
                MappedStatement mappedStatement = interceptorContext.getMappedStatement();
                String sqlId = mappedStatement.getId();
                thread2ExecutingSQLMap.put(Thread.currentThread().getName(), sqlId);
            }
            return invocation.proceed();
        } finally {
            thread2ExecutingSQLMap.remove(Thread.currentThread().getName());
        }
    }
}
