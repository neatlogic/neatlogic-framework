package codedriver.framework.dao.plugin;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.sql.Connection;

/**
 * BG方式查询拦截器
 */
@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})})
public class BusinessGroupInterceptor implements Interceptor {
    Logger logger = LoggerFactory.getLogger(BusinessGroupInterceptor.class);

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler statementHandler = (StatementHandler)invocation.getTarget();

        BoundSql boundSql = statementHandler.getBoundSql();
        // 获取到原始sql语句
        String sql = boundSql.getSql();
        if (sql.contains("BG{")) {
            System.out.println(sql);
            sql = sql.replaceAll("BG\\{([^\\}]+)\\}", "JOIN  bg_target ON $1 = bg_target.target_id AND bg_target.bg_id = 1");
            Field field = boundSql.getClass().getDeclaredField("sql");
            field.setAccessible(true);
            field.set(boundSql, sql);
        }
        return invocation.proceed();
    }

}
