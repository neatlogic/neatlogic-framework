package neatlogic.framework.sqlrunner;

import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.exception.sql.SqlRunnerAccessDeniedException;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.StatementType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.sql.Connection;
import java.sql.SQLException;

/** 仅挂载到动态 SQL 执行器，在缓存读取前和实际 SQL 准备前执行租户隔离。 */
@Intercepts({
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
        @Signature(type = Executor.class, method = "queryCursor", args = {MappedStatement.class, Object.class, RowBounds.class}),
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
        @Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})
})
public class SqlRunnerAccessInterceptor implements Interceptor {
    /** 校验本次实际使用的 SQL 和连接，拒绝写操作及主库回退。 */
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object[] args = invocation.getArgs();
        if (invocation.getTarget() instanceof StatementHandler statementHandler) {
            SqlRunnerAccessPolicy.validateConnection((Connection) args[0]);
            validateBoundSql(statementHandler.getBoundSql());
            return invocation.proceed();
        }
        MappedStatement statement = (MappedStatement) args[0];
        if (statement.getSqlCommandType() != SqlCommandType.SELECT
                || statement.getStatementType() == StatementType.CALLABLE
                || "update".equals(invocation.getMethod().getName())) {
            throw new SqlRunnerAccessDeniedException();
        }
        SqlRunnerAccessPolicy.validateTenant();
        Executor executor = (Executor) invocation.getTarget();
        // 缓存命中也必须检查实际连接，避免旧事务连接或管理库回退绕过检查。
        try {
            SqlRunnerAccessPolicy.validateConnection(executor.getTransaction().getConnection());
        } catch (SQLException ex) {
            // 连接无法确认时失败关闭，不将驱动异常中的连接信息透传给调用方。
            throw new SqlRunnerAccessDeniedException();
        }
        BoundSql boundSql;
        if (args.length == 6) {
            boundSql = (BoundSql) args[5];
        } else {
            boundSql = statement.getBoundSql(args[1]);
        }
        validateBoundSql(boundSql);
        if ("query".equals(invocation.getMethod().getName())) {
            CacheKey key = executor.createCacheKey(statement, args[1], (RowBounds) args[2], boundSql);
            // 同一报表缓存可以跨会话复用，未限定库名的查询也必须按租户隔离。
            key.update(TenantContext.get().getTenantUuid());
            if (args.length == 6) {
                args[4] = key;
            } else {
                // 复用已校验的 BoundSql，避免动态表达式在缓存查询前再次求值。
                return executor.query(statement, args[1], (RowBounds) args[2], (ResultHandler<?>) args[3], key, boundSql);
            }
        }
        return invocation.proceed();
    }

    /** 在解析校验后写回数据仓库占位符替换结果，确保校验和执行使用同一 SQL。 */
    private void validateBoundSql(BoundSql boundSql) {
        String sql = SqlRunnerAccessPolicy.validateSql(boundSql.getSql());
        SystemMetaObject.forObject(boundSql).setValue("sql", sql);
    }
}
