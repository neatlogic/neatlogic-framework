/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.dao.plugin;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.lang.reflect.Field;
import java.util.*;

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

//    private static final List<ResultMapping> EMPTY_RESULTMAPPING = new ArrayList<ResultMapping>(0);
    private Field additionalParametersField;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        //获取拦截方法的参数
        Object[] args = invocation.getArgs();
        MappedStatement ms = (MappedStatement) args[0];
        Object parameterObject = args[1];
        RowBounds rowBounds = (RowBounds) args[2];
        ResultHandler resultHandler = (ResultHandler) args[3];
//        // 判断是否需要进行分页
//        // 没有RowBounds参数时，会使用RowBounds.DEFAULT作为默认值
//        if (rowBounds == RowBounds.DEFAULT) {
//            return invocation.proceed();
//        }
//        if (!(rowBounds instanceof PageRowBounds)) {
//            return invocation.proceed();
//        }
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

//        //根据当前的MappedStatement创建一个返回值为Integer类型的MappedStatement
//        MappedStatement rowNumMs = newMappedStatement(ms, Integer.class);
//        //创建rowNum查询缓存key
//        CacheKey rowNumKey = executor.createCacheKey(rowNumMs, parameterObject, RowBounds.DEFAULT, boundSql);
//        //拼装rowNumSql
//        String rowNumSql = "select count(1) from (" + boundSql.getSql() + ") temp";
//        BoundSql rowNumBoundSql = new BoundSql(ms.getConfiguration(), rowNumSql, boundSql.getParameterMappings(), parameterObject);
//        // 当使用动态Sql时，可能会产生临时的参数
//        // 这些参数需要手动设置到新的BoundSql中
//        for (String key : additionalParameters.keySet()) {
//            rowNumBoundSql.setAdditionalParameter(key, additionalParameters.get(key));
//        }
//        // 执行rowNum查询
//        Object rowNumResultList = executor.query(rowNumMs, parameterObject, RowBounds.DEFAULT, resultHandler, rowNumKey, rowNumBoundSql);
//        Integer rowNum = (Integer) ((List) rowNumResultList).get(0);
//        if (rowNum == 0) {
//            return new ArrayList<>();
//        }
        // 处理rowNum
//        ((PageRowBounds) rowBounds).setRowNum(rowNum);
        // 进行分页查询
        CacheKey pageKey = executor.createCacheKey(ms, parameterObject, RowBounds.DEFAULT, boundSql);
//        String pageSql = boundSql.getSql() + " limit " + rowBounds.getOffset() + "," + rowBounds.getLimit();
        String pageSql = boundSql.getSql() + " limit 10000";
        BoundSql pageBoundSql = new BoundSql(ms.getConfiguration(), pageSql, boundSql.getParameterMappings(), parameterObject);
        for (String key : additionalParameters.keySet()) {
            pageBoundSql.setAdditionalParameter(key, additionalParameters.get(key));
        }
        return executor.query(ms, parameterObject, RowBounds.DEFAULT, resultHandler, pageKey, pageBoundSql);
    }

//    private MappedStatement newMappedStatement(MappedStatement ms, Class<?> resultType) {
//        MappedStatement.Builder builder = new MappedStatement.Builder(ms.getConfiguration(), ms.getId() + "_RowNum", ms.getSqlSource(), ms.getSqlCommandType());
//        builder.resource(ms.getResource())
//                .fetchSize(ms.getFetchSize())
//                .statementType(ms.getStatementType())
//                .keyGenerator(ms.getKeyGenerator())
//                .timeout(ms.getTimeout())
//                .parameterMap(ms.getParameterMap())
//                .resultSetType(ms.getResultSetType())
//                .cache(ms.getCache())
//                .flushCacheRequired(ms.isFlushCacheRequired())
//                .useCache(ms.isUseCache());
//        String[] keyProperties = ms.getKeyProperties();
//        if (keyProperties != null && keyProperties.length > 0) {
//            StringBuilder stringBuilder = new StringBuilder();
//            for (String keyProperty : keyProperties) {
//                stringBuilder.append(keyProperty).append(",");
//            }
//            stringBuilder.delete(stringBuilder.length() - 1, stringBuilder.length());
//            builder.keyProperty(stringBuilder.toString());
//        }
//
//        List<ResultMap> resultMaps = new ArrayList<ResultMap>();
//        ResultMap resultMap = new ResultMap.Builder(ms.getConfiguration(), ms.getId(), resultType, EMPTY_RESULTMAPPING).build();
//        resultMaps.add(resultMap);
//        builder.resultMaps(resultMaps);
//        return builder.build();
//    }
}
