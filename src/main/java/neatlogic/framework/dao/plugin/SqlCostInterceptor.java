/*Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.

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

import neatlogic.framework.asynchronization.threadlocal.RequestContext;
import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.asynchronization.threadlocal.UserContext;
import neatlogic.framework.dto.healthcheck.SqlAuditVo;
import neatlogic.framework.healthcheck.SqlAuditManager;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
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

import java.text.DateFormat;
import java.util.*;
import java.util.regex.Matcher;

/**
 * Sql执行时间记录拦截器
 */
@Intercepts({
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class})
})
public class SqlCostInterceptor implements Interceptor {
    Logger logger = LoggerFactory.getLogger(SqlCostInterceptor.class);

    public static class SqlIdMap {
        private static final Set<String> sqlSet = new HashSet<>();

        public static void addId(String id) {
            sqlSet.add(id);
        }

        public static void removeId(String id) {
            sqlSet.remove(id);
        }

        public static void clear() {
            sqlSet.clear();
        }

        public static List<String> getSqlIdList() {
            return new ArrayList<>(sqlSet);
        }

        public static boolean isExists(String id) {
            if (sqlSet.contains("*")) {
                return true;
            }
            if (sqlSet.contains(id)) {
                return true;
            }
            if (id.contains(".")) {
                id = id.substring(id.lastIndexOf(".") + 1);
            }
            return sqlSet.contains(id);
        }

        public static boolean isEmpty() {
            return sqlSet.isEmpty();
        }
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        long starttime = 0;
        SqlAuditVo sqlAuditVo = null;
        try {
            if (!SqlIdMap.isEmpty()) {
                // Object target = invocation.getTarget();
                MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
                String sqlId = mappedStatement.getId(); // 获取到节点的id,即sql语句的id
                if (SqlIdMap.isExists(sqlId)) {
                    sqlAuditVo = new SqlAuditVo();
                    if (TenantContext.get() != null) {
                        sqlAuditVo.setTenant(TenantContext.get().getTenantUuid());
                    }
                    if (UserContext.get() != null) {
                        sqlAuditVo.setUserId(UserContext.get().getUserId());
                    }
                    starttime = System.currentTimeMillis();
                    Object parameter = null;
                    // 获取参数，if语句成立，表示sql语句有参数，参数格式是map形式
                    if (invocation.getArgs().length > 1) {
                        parameter = invocation.getArgs()[1];
                    }

                    BoundSql boundSql = mappedStatement.getBoundSql(parameter); // BoundSql就是封装myBatis最终产生的sql类
                    Configuration configuration = mappedStatement.getConfiguration(); // 获取节点的配置
                    String sql = getSql(configuration, boundSql, sqlId); // 获取到最终的sql语句
                    //System.out.println("#############################SQL INTERCEPTOR###############################");
                    //System.out.println("id:" + sqlId);
                    //System.out.println(sql);
                    sqlAuditVo.setSql(sql);
                    sqlAuditVo.setId(sqlId);
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        // 执行完上面的任务后，不改变原有的sql执行过程
        Object val = invocation.proceed();
        if (sqlAuditVo != null) {
            sqlAuditVo.setTimeCost(System.currentTimeMillis() - starttime);
            sqlAuditVo.setRunTime(new Date());

            if (val != null) {
                if (val instanceof List) {
                    sqlAuditVo.setRecordCount(((List) val).size());
                } else {
                    sqlAuditVo.setRecordCount(1);
                }
            }
            SqlAuditManager.addSqlAudit(sqlAuditVo);
            RequestContext requestContext =RequestContext.get();
            if (requestContext != null) {
                requestContext.addSqlAudit(sqlAuditVo);
            }
            //System.out.println("time cost:" + (System.currentTimeMillis() - starttime) + "ms");
            //System.out.println("###########################################################################");
        }
        return val;
    }

    // 封装了一下sql语句，使得结果返回完整xml路径下的sql语句节点id + sql语句
    private static String getSql(Configuration configuration, BoundSql boundSql, String sqlId) {
        String sql = showSql(configuration, boundSql);
        if (sql.contains("@{DATA_SCHEMA}")) {
            sql = sql.replace("@{DATA_SCHEMA}", TenantContext.get().getDataDbName());
        }
        return sql;
    }

    // 如果参数是String，则添加单引号， 如果是日期，则转换为时间格式器并加单引号； 对参数是null和不是null的情况作了处理
    private static String getParameterValue(Object obj) {
        String value = null;
        if (obj instanceof String) {
            value = "'" + obj.toString() + "'";
        } else if (obj instanceof Date) {
            DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.CHINA);
            value = "'" + formatter.format(obj) + "'";
        } else {
            if (obj != null) {
                value = obj.toString();
            } else {
                value = "";
            }

        }
        return value;
    }

    // 进行？的替换
    private static String showSql(Configuration configuration, BoundSql boundSql) {
        // 获取参数
        Object parameterObject = boundSql.getParameterObject();
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        // sql语句中多个空格都用一个空格代替
        String sql = boundSql.getSql().replaceAll("[\\s]+", " ");
        if (CollectionUtils.isNotEmpty(parameterMappings) && parameterObject != null) {
            // 获取类型处理器注册器，类型处理器的功能是进行java类型和数据库类型的转换
            TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
            // 如果根据parameterObject.getClass(）可以找到对应的类型，则替换
            if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                sql = sql.replaceFirst("\\?", Matcher.quoteReplacement(getParameterValue(parameterObject)));

            } else {
                // MetaObject主要是封装了originalObject对象，提供了get和set的方法用于获取和设置originalObject的属性值,主要支持对JavaBean、Collection、Map三种类型对象的操作
                MetaObject metaObject = configuration.newMetaObject(parameterObject);
                for (ParameterMapping parameterMapping : parameterMappings) {
                    String propertyName = parameterMapping.getProperty();
                    TypeHandler<?> typeHandler = parameterMapping.getTypeHandler();
                    if (metaObject.hasGetter(propertyName)) {
                        Object obj = metaObject.getValue(propertyName);
                        if (obj != null && typeHandler != null && typeHandler instanceof NeatLogicTypeHandler) {
                            obj = ((NeatLogicTypeHandler) typeHandler).handleParameter(obj);
                        }
                        sql = sql.replaceFirst("\\?", Matcher.quoteReplacement(getParameterValue(obj)));
                    } else if (boundSql.hasAdditionalParameter(propertyName)) {
                        // 该分支是动态sql
                        Object obj = boundSql.getAdditionalParameter(propertyName);
                        if (obj != null && typeHandler != null && typeHandler instanceof NeatLogicTypeHandler) {
                            obj = ((NeatLogicTypeHandler) typeHandler).handleParameter(obj);
                        }
                        sql = sql.replaceFirst("\\?", Matcher.quoteReplacement(getParameterValue(obj)));
                    } else {
                        // 打印出缺失，提醒该参数缺失并防止错位
                        sql = sql.replaceFirst("\\?", "缺失");
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
