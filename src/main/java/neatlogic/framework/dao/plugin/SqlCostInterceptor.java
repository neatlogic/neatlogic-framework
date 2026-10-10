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

import neatlogic.framework.asynchronization.threadlocal.RequestContext;
import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.asynchronization.threadlocal.UserContext;
import neatlogic.framework.dto.healthcheck.SqlAuditVo;
import neatlogic.framework.healthcheck.SqlAuditManager;
import neatlogic.framework.store.mysql.DatasourceManager;
import neatlogic.framework.util.TimeUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;

/**
 * SQL执行时间记录拦截器。
 */
@Intercepts({
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
        @Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class}),
})
public class SqlCostInterceptor implements Interceptor {
    private static final Logger logger = LoggerFactory.getLogger(SqlCostInterceptor.class);
    private static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(TimeUtil.YYYY_MM_DD_HH_MM_SS_SSS);
    // 每次Executor调用独立保存采集上下文，嵌套查询不能覆盖外层的连接及缓存信息。
    private static final ThreadLocal<Deque<SqlExecutionContext>> SQL_EXECUTION_CONTEXT = new ThreadLocal<>();

    private static class SqlExecutionContext {
        private boolean queryFromDatabase;
        private boolean captureDatabase;
        private String datasourceKey;
        private String databaseName;

        /** 先保存当前有效路由，缓存命中时直接使用连接池配置中的库名。 */
        private SqlExecutionContext() {
            try {
                datasourceKey = DatasourceManager.getCurrentDatasourceKey();
                databaseName = DatasourceManager.getDatasourceDatabaseName(datasourceKey);
            } catch (Exception e) {
                // 库配置异常只影响审计信息，不影响原始SQL；避免输出可能含密码的连接配置。
                logger.warn("读取SQL审计数据源配置失败，异常类型：{}", e.getClass().getSimpleName());
            }
        }
    }
    public static class SqlIdMap {
        private static final ConcurrentMap<String, Object> sqlMap = new ConcurrentHashMap<>();

        public static void addId(String id) {
            sqlMap.put(id, new Object());
        }

        public static void removeId(String id) {
            sqlMap.remove(id);
        }

        public static void clear() {
            sqlMap.clear();
        }

        public static List<String> getSqlIdList() {
            return new ArrayList<>(sqlMap.keySet());
        }

        public static boolean isExists(String id) {
            if (sqlMap.containsKey("*")) {
                return true;
            }
            if (sqlMap.containsKey(id)) {
                return true;
            }
            if (id.contains(".")) {
                id = id.substring(id.lastIndexOf(".") + 1);
            }
            return sqlMap.containsKey(id);
        }

        public static boolean isEmpty() {
            return sqlMap.isEmpty();
        }
    }

    public static class UrlMap {
        // URL监控配置和SqlIdMap分开保存，确保两种监控方式互不影响
        private static final ConcurrentMap<String, Object> urlMap = new ConcurrentHashMap<>();

        public static void addUrl(String url) {
            urlMap.put(url, new Object());
        }

        public static void removeUrl(String url) {
            urlMap.remove(url);
        }

        public static void clear() {
            urlMap.clear();
        }

        public static List<String> getUrlList() {
            return new ArrayList<>(urlMap.keySet());
        }

        public static boolean isExists(String url) {
            if (urlMap.containsKey("*")) {
                return true;
            }
            for (Map.Entry<String, Object> entry : urlMap.entrySet()) {
                if (url.contains(entry.getKey())) {
                    return true;
                }
            }
            return false;
        }

        public static boolean isEmpty() {
            return urlMap.isEmpty();
        }
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Method method = invocation.getMethod();
        if (Objects.equals(method.getName(), "prepare")) {
            recordPreparedConnection((Connection) invocation.getArgs()[0]);
            return invocation.proceed();
        }
        Deque<SqlExecutionContext> contexts = SQL_EXECUTION_CONTEXT.get();
        if (contexts == null) {
            contexts = new ArrayDeque<>();
            SQL_EXECUTION_CONTEXT.set(contexts);
        }
        SqlExecutionContext context = new SqlExecutionContext();
        contexts.push(context);
        try {
            return interceptExecutor(invocation, context);
        } finally {
            contexts.pop();
            if (contexts.isEmpty()) {
                SQL_EXECUTION_CONTEXT.remove();
            }
        }
    }

    /** 根据真正用于prepare的连接修正库信息，采集异常不能影响原SQL的执行。 */
    private void recordPreparedConnection(Connection connection) {
        Deque<SqlExecutionContext> contexts = SQL_EXECUTION_CONTEXT.get();
        if (contexts == null || contexts.isEmpty()) {
            return;
        }
        SqlExecutionContext context = contexts.peek();
        context.queryFromDatabase = true;
        if (!context.captureDatabase) {
            return;
        }
        try {
            String catalog = connection.getCatalog();
            if (StringUtils.isNotBlank(catalog)) {
                context.databaseName = catalog;
            }
            context.datasourceKey = DatasourceManager.getDatasourceKey(connection, context.datasourceKey);
            if (StringUtils.isBlank(catalog) && context.datasourceKey != null) {
                context.databaseName = DatasourceManager.getDatasourceDatabaseName(context.datasourceKey);
            }
        } catch (Exception e) {
            // 实际连接身份没有确认时清空目标键，避免后续执行计划误用逻辑路由。
            context.datasourceKey = null;
            // 驱动异常信息可能含完整连接URL，采集日志只保留异常类型。
            logger.warn("采集SQL执行数据库信息失败，异常类型：{}", e.getClass().getSimpleName());
        }
    }

    /** 执行原始MyBatis调用，并将当前调用的数据库、耗时和缓存信息写入审计记录。 */
    private Object interceptExecutor(Invocation invocation, SqlExecutionContext context) throws Throwable {
        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        long starttime = 0;
        SqlAuditVo sqlAuditVo = null;
        boolean isMonitorSqlId = false;
        boolean isMonitorUrl = false;
        boolean hasCacheFirstLevel = false;
        String requestUrl = null;
        try {
            String sqlId = mappedStatement.getId();
            isMonitorSqlId = !SqlIdMap.isEmpty() && SqlIdMap.isExists(sqlId);
            // URL监控依赖RequestContext，精确匹配当前HTTP请求的URL，*代表监控全部请求
            if (RequestContext.get() != null) {
                requestUrl = RequestContext.get().getUrl();
                isMonitorUrl = StringUtils.isNotBlank(requestUrl) && !UrlMap.isEmpty() && UrlMap.isExists(requestUrl);
            }
            if (isMonitorSqlId || isMonitorUrl) {
                context.captureDatabase = true;
                sqlAuditVo = buildSqlAuditVo(invocation, mappedStatement, sqlId);
                starttime = System.currentTimeMillis();
                // 两种监控方式都会展示缓存命中情况，所以只要命中任意监控都需要计算缓存级别
                if (Objects.equals(invocation.getMethod().getName(), "query")) {
                    hasCacheFirstLevel = hasCacheFirstLevel(invocation, mappedStatement);
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        // 执行完上面的记录准备后，不改变原有SQL执行过程。
        Object val = invocation.proceed();
        if (sqlAuditVo != null) {
            fillSqlAuditResult(sqlAuditVo, starttime, hasCacheFirstLevel, val, context);
            if (isMonitorSqlId) {
                // sqlId监控沿用原明细表，一条SQL执行记录对应表格一行。
                SqlAuditManager.addSqlAudit(sqlAuditVo);
            }
            if (isMonitorUrl && RequestContext.get() != null) {
                // URL监控保留每次SQL执行的数据源，同一SQL ID在不同库执行仍有独立明细。
                RequestContext.get().addSqlAudit(sqlAuditVo);
            }
        }
        return val;
    }

    private SqlAuditVo buildSqlAuditVo(Invocation invocation, MappedStatement mappedStatement, String sqlId) {
        SqlAuditVo sqlAuditVo = new SqlAuditVo();
        sqlAuditVo.setThreadName(Thread.currentThread().getName());
        if (TenantContext.get() != null) {
            sqlAuditVo.setTenant(TenantContext.get().getTenantUuid());
        }
        if (UserContext.get() != null) {
            sqlAuditVo.setUserId(UserContext.get().getUserId());
        }
        Object parameter = null;
        // SQL监控需要还原最终执行语句，因此这里保留原有参数读取方式
        if (invocation.getArgs().length > 1) {
            parameter = invocation.getArgs()[1];
        }
        sqlAuditVo.setSql(getSql(mappedStatement, parameter));
        sqlAuditVo.setId(sqlId);
        return sqlAuditVo;
    }

    private boolean hasCacheFirstLevel(Invocation invocation, MappedStatement mappedStatement) {
        CacheKey key = null;
        Executor executor = (Executor) invocation.getTarget();
        Object[] args = invocation.getArgs();
        if (args.length > 4) {
            key = (CacheKey) args[4];
        } else if (args.length == 4) {
            Object parameterObject = args[1];
            RowBounds rowBounds = (RowBounds) args[2];
            key = executor.createCacheKey(mappedStatement, parameterObject, rowBounds, mappedStatement.getBoundSql(parameterObject));
        }
        return executor.isCached(mappedStatement, key);
    }

    /** 将当前调用独立保存的执行库和缓存信息填入明细，缺失配置保持为空。 */
    private void fillSqlAuditResult(SqlAuditVo sqlAuditVo, long starttime, boolean hasCacheFirstLevel, Object val,
                                   SqlExecutionContext context) {
        sqlAuditVo.setDatasourceKey(context.datasourceKey);
        sqlAuditVo.setDatabaseName(context.databaseName);
        if (context.queryFromDatabase) {
            // SQL语句被实际执行，说明没有使用缓存
            sqlAuditVo.setUseCacheLevel(StringUtils.EMPTY);
        } else if (hasCacheFirstLevel) {
            sqlAuditVo.setUseCacheLevel("一级缓存");
        } else {
            sqlAuditVo.setUseCacheLevel("二级缓存");
        }
        sqlAuditVo.setTimeCost(System.currentTimeMillis() - starttime);
        sqlAuditVo.setRunTime(new Date());
        if (val != null) {
            if (val instanceof List) {
                sqlAuditVo.setRecordCount(((List<?>) val).size());
            } else {
                sqlAuditVo.setRecordCount(1);
            }
        }
    }

    public static String getSql(MappedStatement mappedStatement, Object parameterObject) {
        Configuration configuration = mappedStatement.getConfiguration();
        BoundSql boundSql = mappedStatement.getBoundSql(parameterObject);
        String sql = showSql(configuration, boundSql);
        if (sql.contains("@{DATA_SCHEMA}") && TenantContext.get() != null) {
            sql = sql.replace("@{DATA_SCHEMA}", TenantContext.get().getDataDbName());
        }
        return sql;
    }

    // 如果参数是String则添加单引号，如果是日期则转换为时间格式并加单引号，null参数写成NULL
    private static String getParameterValue(Object obj) {
        String value = null;
        if (obj instanceof String) {
            value = "'" + obj + "'";
        } else if (obj instanceof Date date) {
//            DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.CHINA);
//            value = "'" + formatter.format(obj) + "'";
            LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            value = "'" + localDateTime.format(dateTimeFormatter) + "'";
        } else {
            if (obj != null) {
                value = obj.toString();
            } else {
                value = "NULL";
            }
        }
        return value;
    }

    // 进行?占位符替换，生成用于SQL监控展示的最终SQL语句
    private static String showSql(Configuration configuration, BoundSql boundSql) {
        Object parameterObject = boundSql.getParameterObject();
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        String sql = boundSql.getSql().replaceAll("[\\s]+", " ");
        if (CollectionUtils.isNotEmpty(parameterMappings) && parameterObject != null) {
            // 匹配除了在单引号内的所有问号
            String regex = "\\?(?=(?:[^']*'[^']*')*[^']*$)";
            TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
            if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                sql = sql.replaceFirst("\\?", Matcher.quoteReplacement(getParameterValue(parameterObject)));
            } else {
                MetaObject metaObject = configuration.newMetaObject(parameterObject);
                for (ParameterMapping parameterMapping : parameterMappings) {
                    String propertyName = parameterMapping.getProperty();
                    TypeHandler<?> typeHandler = parameterMapping.getTypeHandler();
                    if (metaObject.hasGetter(propertyName)) {
                        Object obj = metaObject.getValue(propertyName);
                        if (obj != null && typeHandler instanceof NeatLogicTypeHandler) {
                            obj = ((NeatLogicTypeHandler) typeHandler).handleParameter(obj);
                        }
                        sql = sql.replaceFirst(regex, Matcher.quoteReplacement(getParameterValue(obj)));
                    } else if (boundSql.hasAdditionalParameter(propertyName)) {
                        // 动态SQL参数会放在additionalParameter中，这里保持原有替换逻辑
                        Object obj = boundSql.getAdditionalParameter(propertyName);
                        if (obj != null && typeHandler instanceof NeatLogicTypeHandler) {
                            obj = ((NeatLogicTypeHandler) typeHandler).handleParameter(obj);
                        }
                        sql = sql.replaceFirst(regex, Matcher.quoteReplacement(getParameterValue(obj)));
                    } else {
                        // 参数缺失时保留明确占位，防止后续参数错位
                        sql = sql.replaceFirst(regex, "缺失");
                    }
                }
            }
        }
        return sql;
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }
}
