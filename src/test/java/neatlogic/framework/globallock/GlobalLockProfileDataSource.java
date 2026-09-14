/*
 * Copyright (C) 2026  深圳极向量科技有限公司 All Rights Reserved.
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

package neatlogic.framework.globallock;

import org.springframework.jdbc.datasource.AbstractDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.sql.DataSource;
import java.sql.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;

/** 仅用于压测分解 JDBC 耗时，所有调用都转交原连接池，不修改生产 SQL 或事务。 */
final class GlobalLockProfileDataSource extends AbstractDataSource {
    private static final Logger logger = LoggerFactory.getLogger(GlobalLockProfileDataSource.class);
    private final DataSource delegate;
    static final ThreadLocal<Sample> CURRENT = new ThreadLocal<>();
    static final Map<String, Counters> SQL = new ConcurrentHashMap<>();
    private static final Map<String, String> TEMPLATES = new ConcurrentHashMap<>();

    /** 复用原数据源，确保事务管理器与 MyBatis 使用同一代理实例。 */
    GlobalLockProfileDataSource(DataSource delegate) { this.delegate = delegate; }

    /** 一个周期的各阶段耗时，单位纳秒；数据库各项不包含在彼此之内。 */
    static final class Sample {
        final String label;
        int phase;
        final long[] phaseTime = new long[3];
        final long[][] time = new long[3][5];
        final int[][] count = new int[3][5];
        /** 标记范围、轮次和版本，供离线分析对应采样窗口。 */
        Sample(String label) { this.label = label; }
        /** 按阶段累加同一线程上的数据库调用。 */
        void add(int category, long nanos) { time[phase][category] += nanos; count[phase][category]++; }
        /** 输出数值样本，便于分析慢周期自身的耗时构成。 */
        String row(long total) {
            StringBuilder row = new StringBuilder(label).append(',').append(total);
            for (int phase=0; phase<3; phase++) {
                row.append(',').append(phaseTime[phase]);
                for (int kind=0; kind<5; kind++) row.append(',').append(time[phase][kind]).append(',').append(count[phase][kind]);
            }
            return row.toString();
        }
    }

    /** 汇总 SQL 模板耗时，不保存绑定参数。 */
    static final class Counters {
        final LongAdder calls = new LongAdder();
        final LongAdder nanos = new LongAdder();
    }

    /** 计量从 Hikari 取得连接的时间，不包含后续 JDBC 调用。 */
    @Override public Connection getConnection() throws SQLException {
        long start = System.nanoTime();
        Connection connection;
        try { connection = delegate.getConnection(); }
        finally { add(0, System.nanoTime()-start); }
        return (Connection) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{Connection.class}, (proxy, method, args) -> {
            String name = method.getName();
            int category = "commit".equals(name) || "rollback".equals(name) ? 2
                    : "setAutoCommit".equals(name) || "setTransactionIsolation".equals(name) ? 3 : 4;
            long before = System.nanoTime(); Object result;
            try { result = invoke(connection, method, args); }
            finally { add(category, System.nanoTime()-before); }
            if (result instanceof PreparedStatement) return statement((Statement) result, (String) args[0], PreparedStatement.class);
            if (result instanceof Statement) return statement((Statement) result, null, Statement.class);
            return result;
        });
    }

    /** 测试中禁止通过额外凭据绕过统一连接池。 */
    @Override public Connection getConnection(String user, String password) throws SQLException { return getConnection(); }

    /** 拦截真正执行 SQL 的调用，参数设置及结果映射时间仍留在阶段剩余耗时中。 */
    private Object statement(Statement statement, String sql, Class<?> type) {
        return Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{type}, (proxy, method, args) -> {
            if (!method.getName().startsWith("execute")) return invoke(statement, method, args);
            long before = System.nanoTime();
            try { return invoke(statement, method, args); }
            finally {
                long elapsed = System.nanoTime()-before; add(1, elapsed);
                Sample sample = CURRENT.get();
                if (sample != null) {
                    String template = sql == null ? String.valueOf(args[0]) : sql;
                    String key = sample.label+"|"+sample.phase+"|"+TEMPLATES.computeIfAbsent(template, text -> text.replaceAll("\\s+", " ").trim());
                    Counters counters = SQL.computeIfAbsent(key, ignored -> new Counters());
                    counters.calls.increment(); counters.nanos.add(elapsed);
                }
            }
        });
    }

    /** 保留原始数据库异常类型，避免代理改变事务重试分支。 */
    private Object invoke(Object target, Method method, Object[] args) throws Throwable {
        try { return method.invoke(target, args); }
        catch (InvocationTargetException ex) { logger.error("Profiled JDBC call failed", ex.getCause()); throw ex.getCause(); }
    }

    /** 非采样周期仅转交原调用，不计入统计。 */
    private static void add(int category, long elapsed) {
        Sample sample = CURRENT.get(); if (sample != null) sample.add(category, elapsed);
    }
}
