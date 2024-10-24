package neatlogic.framework.dao.plugin;

import neatlogic.framework.asynchronization.threadlocal.LicensePolicyContext;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * license规则查询拦截器
 */
@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})})
public class LicensePolicyInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        // 获取目标对象
        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();

        // 通过MetaObject来访问StatementHandler内部属性
        MetaObject metaObject = SystemMetaObject.forObject(statementHandler);
        metaObject = getTarget(metaObject);
        // 获取 MappedStatement
        MappedStatement mappedStatement = (MappedStatement) metaObject.getValue("delegate.mappedStatement");
        String sqlId = mappedStatement.getId(); // 获取到节点的id,即sql语句的id
        BoundSql boundSql = statementHandler.getBoundSql();
        // 获取到原始sql语句
        String sql = boundSql.getSql();
        if (sql.contains("LP{")) {
            if (LicensePolicyContext.get() == null
                    || MapUtils.isEmpty(LicensePolicyContext.get().getSqlPolicyValue())
                    || !LicensePolicyContext.get().getSqlPolicyValue().containsKey(sqlId)
                    || MapUtils.isEmpty(LicensePolicyContext.get().getSqlPolicyValue().get(sqlId))
            ) {
                sql = sql.replaceAll("and\\s+LP\\{[^\\}]+\\}", StringUtils.EMPTY).replaceAll("LP\\{[^\\}]+\\}", StringUtils.EMPTY);
                if (sql.toLowerCase().trim().endsWith("where")) {
                    sql = sql.trim().substring(0, sql.length() - 7);
                }
            } else {
                Map<String, Long> policyValueMap = LicensePolicyContext.get().getSqlPolicyValue().get(sqlId);
                String condition = StringUtils.EMPTY;
                String policy = null;
                String regEx = "LP\\{([^\\}]+)\\}";
                Pattern pat = Pattern.compile(regEx);
                Matcher mat = pat.matcher(sql);
                while (mat.find()) {
                    String atmp = mat.group(0);
                    String tmpStr = mat.group(1);
                    if (StringUtils.isNotBlank(tmpStr)) {
                        String[] tmp = tmpStr.split("=");
                        if (tmp.length == 2) {
                            condition = tmp[0];
                            policy = tmp[1];
                        }
                    }
                    if (Objects.equals(policy, "maxLimitId") && policyValueMap.containsKey(policy)) {
                        sql = sql.replaceAll("LP\\{" + tmpStr + "\\}", String.format("%s <= %d", condition, policyValueMap.get(policy)));
                    }
                }
            }
            Field field = boundSql.getClass().getDeclaredField("sql");
            field.setAccessible(true);
            field.set(boundSql, sql);
        }
        return invocation.proceed();
    }

    /**
     * 递归解包代理对象，直到获取实际的目标对象
     */
    private MetaObject getTarget(MetaObject metaObject) throws Exception {
        if (metaObject.hasGetter("h")) {
            Object proxyHandler = metaObject.getValue("h");
            metaObject = SystemMetaObject.forObject(proxyHandler);
            if (metaObject.hasGetter("target")) {
                Object target = metaObject.getValue("target");
                return getTarget(SystemMetaObject.forObject(target));
            }
        }
        return metaObject;
    }
}
