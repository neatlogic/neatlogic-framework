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

import org.apache.commons.collections4.CollectionUtils;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.StringTypeHandler;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Intercepts({
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
        @Signature(type = ResultSetHandler.class, method = "handleResultSets", args = {Statement.class})
})
public class ModifyResultMapTypeHandlerInterceptor implements Interceptor {

    Logger logger = LoggerFactory.getLogger(ModifyResultMapTypeHandlerInterceptor.class);

    private static final ThreadLocal<MappedStatement> mappedStatementThreadLocal = new ThreadLocal<>();

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Method method = invocation.getMethod();
        if (Objects.equals(method.getName(), "query")) {
            try {
                mappedStatementThreadLocal.set((MappedStatement) invocation.getArgs()[0]);
                return invocation.proceed();
            } finally {
                mappedStatementThreadLocal.remove();
            }
        } else {
            try {
                MappedStatement mappedStatement = mappedStatementThreadLocal.get();
                if (mappedStatement != null) {
                    Configuration configuration = mappedStatement.getConfiguration();
                    int resultMappingSize = 0;
                    List<ResultMap> resultMaps = mappedStatement.getResultMaps();
                    for (ResultMap resultMap : resultMaps) {
                        resultMappingSize += resultMap.getResultMappings().size();
                    }
                    if (resultMappingSize > 0) {
                        List<String> longVarcharColumnLabelList = new ArrayList<>();
                        PreparedStatement ps = (PreparedStatement) invocation.getArgs()[0];
                        ResultSet rs = ps.getResultSet();
                        final ResultSetMetaData metaData = rs.getMetaData();
                        final int columnCount = metaData.getColumnCount();
                        for (int i = 1; i <= columnCount; i++) {
                            int columnType = metaData.getColumnType(i);
                            JdbcType jdbcType = JdbcType.forCode(columnType);
                            if (jdbcType == JdbcType.LONGVARCHAR) {
                                String columnLabel = metaData.getColumnLabel(i);
                                longVarcharColumnLabelList.add(columnLabel);
                            }
                        }
                        if (CollectionUtils.isNotEmpty(longVarcharColumnLabelList)) {
                            for (ResultMap resultMap : resultMaps) {
                                handleResultMap(longVarcharColumnLabelList, configuration, resultMap);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
            return invocation.proceed();
        }
    }

    private void handleResultMap(List<String> longVarcharColumnLabelList, Configuration configuration, ResultMap resultMap) throws NoSuchFieldException, IllegalAccessException {
        List<ResultMapping> resultMappings = resultMap.getResultMappings();
        if (CollectionUtils.isNotEmpty(resultMappings)) {
            TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
            for (ResultMapping resultMapping : resultMappings) {
                if (longVarcharColumnLabelList.contains(resultMapping.getColumn())) {
                    TypeHandler<?> typeHandler = resultMapping.getTypeHandler();
                    if (typeHandler instanceof StringTypeHandler) {
                        Field typeHandlerField = resultMapping.getClass().getDeclaredField("typeHandler");
                        typeHandlerField.setAccessible(true);
                        typeHandlerField.set(resultMapping, typeHandlerRegistry.getTypeHandler(String.class, JdbcType.LONGVARCHAR));
                    }
                } else {
                    String nestedResultMapId = resultMapping.getNestedResultMapId();
                    if (nestedResultMapId != null) {
                        handleResultMap(longVarcharColumnLabelList, configuration, configuration.getResultMap(nestedResultMapId));
                    }
                }
            }
        }
    }
}
