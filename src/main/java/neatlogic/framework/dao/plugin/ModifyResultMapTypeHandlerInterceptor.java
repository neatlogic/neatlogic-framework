/*
 * Copyright (C) 2025  深圳极向量科技有限公司 All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package neatlogic.framework.dao.plugin;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.StringTypeHandler;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Intercepts({
        @Signature(type = ResultSetHandler.class, method = "handleResultSets", args = {Statement.class})
})
public class ModifyResultMapTypeHandlerInterceptor implements Interceptor {

    Logger logger = LoggerFactory.getLogger(ModifyResultMapTypeHandlerInterceptor.class);
    public static final ThreadLocal<MappedStatement> mappedStatementThreadLocal = new ThreadLocal<>();

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
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
        } finally {
            mappedStatementThreadLocal.remove();
        }
        return invocation.proceed();
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
