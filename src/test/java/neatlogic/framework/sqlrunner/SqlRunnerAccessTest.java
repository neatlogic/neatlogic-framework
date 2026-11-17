package neatlogic.framework.sqlrunner;

import com.zaxxer.hikari.HikariConfig;
import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.dao.plugin.PageInterceptor;
import neatlogic.framework.dao.plugin.PageRowBounds;
import neatlogic.framework.exception.sql.SqlRunnerAccessDeniedException;
import neatlogic.framework.store.mysql.DatasourceManager;
import neatlogic.framework.store.mysql.NeatLogicBasicDataSource;
import org.apache.ibatis.builder.StaticSqlSource;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.scripting.xmltags.XMLLanguageDriver;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.transaction.Transaction;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/** 报表 SQL 的解析、连接、分页和缓存边界测试，所有连接均为替身。 */
public class SqlRunnerAccessTest {
    private static final String TENANT = "sql_policy_fixture";
    private static final String DB = "neatlogic_" + TENANT;
    private static final String DATA_DB = DB + "_data";
    private Map<Object, Object> routes;
    private Object originalRoute;
    private Object originalDataRoute;
    private TenantContext originalContext;
    private final List<NeatLogicBasicDataSource> createdSources = new ArrayList<>();

    /** 注册测试租户的连接池配置，启动空池且无需访问数据库。 */
    @Before
    @SuppressWarnings("unchecked")
    public void setUp() throws Exception {
        originalContext = TenantContext.get();
        Field field = DatasourceManager.class.getDeclaredField("datasourceMap");
        field.setAccessible(true);
        routes = (Map<Object, Object>) field.get(null);
        originalRoute = routes.put(TENANT, source(DB));
        originalDataRoute = routes.put(TENANT + "_DATA", source(DATA_DB));
        TenantContext.init(TENANT);
    }

    /** 恢复共享注册表与线程上下文，避免影响其他测试。 */
    @After
    public void tearDown() {
        restoreRoute(TENANT, originalRoute);
        restoreRoute(TENANT + "_DATA", originalDataRoute);
        for (NeatLogicBasicDataSource source : createdSources) {
            source.close();
        }
        TenantContext.get().release();
        TenantContext.init(originalContext);
    }

    /** 当前租户的两库、占位符、常用表达式及复杂查询应继续可用。 */
    @Test
    public void shouldAllowCurrentTenantQueries() {
        List<String> queries = Arrays.asList(
                "select 1", "select * from issue", "select * from " + DB + ".issue",
                "select * from `" + DATA_DB + "`.`records`",
                "SeLeCt * FrOm `" + DB + "`.`issue`",
                "select * from @{DATA_SCHEMA}.records where id = ?",
                "select a.id from issue a join " + DATA_DB + ".records b on a.id = b.id",
                "select id from issue union all select id from " + DATA_DB + ".records",
                "with x as (select id from issue) select * from x",
                "with x as (select id from issue), y as (select * from x) select * from y",
                "select * from (select * from issue) x where exists (select 1 from issue y where y.id=x.id)",
                "select count(1) from (select id from " + DB + ".issue) temp",
                "select coalesce(a.name, 'neatlogic.mongodb') from issue a group by a.name order by a.name limit 0,10",
                "select /* 普通说明 neatlogic.mongodb */ 1"
        );
        for (String query : queries) {
            String validated = SqlRunnerAccessPolicy.validateSql(query);
            Assert.assertNotNull(query, validated);
            Assert.assertFalse(validated.contains("@{DATA_SCHEMA}"));
        }
    }

    /** 管理库、系统库和其他租户库均不能进入执行流程。 */
    @Test
    public void shouldDenyForbiddenSchemasAtEveryQueryPosition() {
        for (String table : Arrays.asList("neatlogic.mongodb", "neatlogic.elasticsearch", "neatlogic.datasource",
                "`NEATLOGIC`.`mongodb`", "`" + DB.toUpperCase() + "`.issue", "neatlogic_another.issue", "s.." + DB + ".issue", "mysql.user", "information_schema.tables", "sys.version")) {
            List<String> queries = Arrays.asList(
                    "select * from " + table,
                    "select a.id from issue a join " + table + " b on a.id=b.id",
                    "select id from issue union all select id from " + table,
                    "select * from issue where exists (select 1 from " + table + ")",
                    "select (select count(*) from " + table + ") as total",
                    "select * from issue order by (select count(*) from " + table + ")",
                    "select id from issue group by (select count(*) from " + table + "),id",
                    "select a.id from issue a join issue b on a.id=(select count(*) from " + table + ")",
                    "with x as (select * from " + table + ") select * from x",
                    "select count(1) from (select * from " + table + ") temp"
            );
            for (String query : queries) {
                deny(() -> SqlRunnerAccessPolicy.validateSql(query));
            }
        }
    }

    /** 同名 CTE 不能掩盖显式指定管理库的真实表引用。 */
    @Test
    public void shouldNotConfuseCteNamesWithQualifiedTables() {
        SqlRunnerAccessPolicy.validateSql("with mongodb as (select 1 as id) select * from mongodb");
        deny(() -> SqlRunnerAccessPolicy.validateSql("with mongodb as (select 1 as id) select * from neatlogic.mongodb"));
        deny(() -> SqlRunnerAccessPolicy.validateSql("with x as (select * from neatlogic.mongodb) select * from (with x as (select 1) select * from x) y"));
    }

    /** 只接受单条可解析查询，禁止写入、多语句和带副作用的 SELECT。 */
    @Test
    public void shouldRejectNonQueryAndAmbiguousSyntax() {
        for (String query : Arrays.asList("select 1; select * from neatlogic.mongodb", "delete from issue",
                "update issue set id=1", "insert into issue values (1)", "call run_report()",
                "show databases", "use neatlogic", "select * into outfile '/tmp/sql-policy' from issue",
                "select * into dumpfile '/tmp/sql-policy' from issue", "select id into @secret from issue",
                "select * from", "", "select /*!50000 * from neatlogic.mongodb */ 1",
                "select /*M!50000 * from neatlogic.mongodb */ 1",
                "select 1 /*T! UNION SELECT 1 FROM neatlogic.mongodb */", "select LOAD_FILE('/etc/passwd')",
                "select id from issue for update", "select neatlogic.get_password()")) {
            deny(() -> SqlRunnerAccessPolicy.validateSql(query));
        }
    }

    /** 解析器和数据库对注释、引用及反斜杠的不同解释不能形成跨库绕过。 */
    @Test
    public void shouldRejectDialectAmbiguitiesAndKeepBoundParameters() throws Exception {
        for (String query : Arrays.asList(
                "select ascii(substr(password,1,1))--0 from neatlogic.datasource",
                "select 'a\\' + (SELECT count(*) FROM neatlogic.datasource) -- '",
                "select password $$ from neatlogic.datasource -- $$",
                "select 1 // union select 1 from neatlogic.datasource",
                "select 1 --0", "select 'C:\\reports'")) {
            deny(() -> SqlRunnerAccessPolicy.validateSql(query));
        }
        for (String query : Arrays.asList(
                "select 1 -- 标准注释\n", "select 1 -- 注释\n from issue",
                "select 'http://example.invalid/path'", "select '--0'", "select '$$'")) {
            Assert.assertEquals(query, SqlRunnerAccessPolicy.validateSql(query));
        }
        Configuration configuration = new Configuration();
        MappedStatement statement = new MappedStatement.Builder(configuration, "fixture.boundLiteral",
                new XMLLanguageDriver().createSqlSource(configuration, "<script>select #{value} as value</script>", Map.class),
                SqlCommandType.SELECT).build();
        AtomicInteger executed = new AtomicInteger();
        Executor executor = guardedExecutor(connection(DB), executed, new ArrayList<>());
        executor.query(statement, Collections.singletonMap("value", "C:\\reports\\example"), RowBounds.DEFAULT, null);
        Assert.assertEquals("绑定参数的反斜杠不得影响 SQL 词法校验", 1, executed.get());
    }

    /** 无租户、缺少路由和被切到管理库的连接必须失败关闭。 */
    @Test
    public void shouldDenyMissingTenantRouteAndManagementCatalog() {
        TenantContext.init();
        deny(SqlRunnerAccessPolicy::validateTenant);
        TenantContext.init(TENANT);
        routes.remove(TENANT);
        deny(SqlRunnerAccessPolicy::validateTenant);
        routes.put(TENANT, source(DB));
        SqlRunnerAccessPolicy.validateConnection(connection(DB));
        SqlRunnerAccessPolicy.validateConnection(connection(DATA_DB));
        deny(() -> SqlRunnerAccessPolicy.validateConnection(connection("neatlogic")));
        deny(() -> SqlRunnerAccessPolicy.validateConnection(connection("neatlogic_another")));
        deny(() -> SqlRunnerAccessPolicy.validateConnection(connection(null)));
        deny(() -> SqlRunnerAccessPolicy.validateConnection(null));
    }

    /** 数据仓库路由同样必须注册且配置正确，不能回退到管理库。 */
    @Test
    public void shouldValidateDataRouteAndConfiguredDatabase() {
        TenantContext.get().switchDataDatabase();
        SqlRunnerAccessPolicy.validateTenant();
        routes.remove(TENANT + "_DATA");
        deny(SqlRunnerAccessPolicy::validateTenant);
        routes.put(TENANT + "_DATA", source("neatlogic"));
        deny(() -> SqlRunnerAccessPolicy.validateConnection(connection("neatlogic")));
    }

    /** 原样参数替换和动态条件最终生成的 SQL 均要重新校验。 */
    @Test
    public void shouldValidateExpandedDynamicSqlBeforeCacheLookup() throws Exception {
        Configuration configuration = new Configuration();
        MappedStatement statement = new MappedStatement.Builder(configuration, "fixture.dynamic",
                new XMLLanguageDriver().createSqlSource(configuration,
                        "<script>select * from ${table}<if test='filter != null'> where id = #{filter}</if></script>", Map.class),
                SqlCommandType.SELECT).build();
        AtomicInteger executed = new AtomicInteger();
        Executor executor = guardedExecutor(connection(DB), executed, new ArrayList<>());
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("table", "issue");
        parameters.put("filter", 1);
        executor.query(statement, parameters, RowBounds.DEFAULT, null);
        Assert.assertEquals(1, executed.get());
        parameters.put("table", "neatlogic.mongodb");
        deny(() -> executor.query(statement, parameters, RowBounds.DEFAULT, null));
        Assert.assertEquals("非法动态 SQL 不能到达执行器或缓存", 1, executed.get());
    }

    /** 即使底层执行器会直接返回缓存，越界 SQL 和错误连接也必须先被拒绝。 */
    @Test
    public void shouldGuardBothQueryOverloadsAndCachedConnections() throws Exception {
        AtomicInteger executed = new AtomicInteger();
        Executor executor = guardedExecutor(connection(DB), executed, new ArrayList<>());
        MappedStatement forbidden = statement("select * from neatlogic.datasource");
        deny(() -> executor.query(forbidden, null, RowBounds.DEFAULT, null));
        MappedStatement safe = statement("select 1");
        BoundSql replaced = new BoundSql(safe.getConfiguration(), "select * from neatlogic.elasticsearch", Collections.emptyList(), null);
        deny(() -> executor.query(safe, null, RowBounds.DEFAULT, null, new CacheKey(), replaced));
        Assert.assertEquals(0, executed.get());
        Executor badConnection = guardedExecutor(connection("neatlogic"), executed, new ArrayList<>());
        deny(() -> badConnection.query(safe, null, RowBounds.DEFAULT, null));
        Assert.assertEquals(0, executed.get());
    }

    /** 不带库名前缀的相同查询在不同租户中必须使用不同缓存键。 */
    @Test
    public void shouldSeparateCacheKeysByTenant() throws Exception {
        String anotherTenant = TENANT + "_another";
        Object prior = routes.put(anotherTenant, source("neatlogic_" + anotherTenant));
        List<CacheKey> keys = new ArrayList<>();
        try {
            Transaction transaction = proxy(Transaction.class, (target, method, args) -> {
                if ("getConnection".equals(method.getName())) {
                    return connection(TenantContext.get().getDbName());
                }
                return null;
            });
            Executor delegate = proxy(Executor.class, (target, method, args) -> {
                if ("getTransaction".equals(method.getName())) {
                    return transaction;
                }
                if ("createCacheKey".equals(method.getName())) {
                    CacheKey key = new CacheKey();
                    key.update(((BoundSql) args[3]).getSql());
                    return key;
                }
                if ("query".equals(method.getName())) {
                    keys.add((CacheKey) args[4]);
                    return Collections.singletonList(1);
                }
                return null;
            });
            Executor guarded = (Executor) new SqlRunnerAccessInterceptor().plugin(delegate);
            MappedStatement query = statement("select id from issue");
            guarded.query(query, null, RowBounds.DEFAULT, null);
            TenantContext.init(anotherTenant);
            guarded.query(query, null, RowBounds.DEFAULT, null);
            Assert.assertEquals(2, keys.size());
            Assert.assertNotEquals(keys.get(0), keys.get(1));
        } finally {
            restoreRoute(anotherTenant, prior);
            TenantContext.init(TENANT);
        }
    }

    /** 分页内部的计数 SQL 和数据 SQL 均经过相同的租户校验。 */
    @Test
    public void shouldGuardGeneratedPaginationSql() throws Exception {
        AtomicInteger executed = new AtomicInteger();
        List<String> sqlList = new ArrayList<>();
        Executor executor = (Executor) new PageInterceptor().plugin(guardedExecutor(connection(DB), executed, sqlList));
        PageRowBounds bounds = new PageRowBounds(0, 10);
        executor.query(statement("select id from @{DATA_SCHEMA}.records"), null, bounds, null);
        Assert.assertEquals(2, executed.get());
        Assert.assertTrue(sqlList.get(0).startsWith("select count(1) from ("));
        Assert.assertTrue(sqlList.get(0).contains(DATA_DB + ".records"));
        Assert.assertTrue(sqlList.get(1).endsWith("limit 0,10"));
        Assert.assertEquals(1, bounds.getRowNum());
        deny(() -> executor.query(statement("select * from neatlogic.mongodb"), null, new PageRowBounds(0, 10), null));
        Assert.assertEquals(2, executed.get());
    }

    /** StatementHandler 最后一道校验应写回占位符，并在错误连接上阻止准备语句。 */
    @Test
    public void shouldCheckPrepareAndWriteBackExpandedSql() throws Exception {
        BoundSql boundSql = new BoundSql(new Configuration(), "select * from @{DATA_SCHEMA}.records", Collections.emptyList(), null);
        AtomicInteger prepared = new AtomicInteger();
        StatementHandler delegate = proxy(StatementHandler.class, (target, method, args) -> {
            if ("getBoundSql".equals(method.getName())) {
                return boundSql;
            }
            if ("prepare".equals(method.getName())) {
                prepared.incrementAndGet();
            }
            return null;
        });
        StatementHandler guarded = (StatementHandler) new SqlRunnerAccessInterceptor().plugin(delegate);
        guarded.prepare(connection(DB), null);
        Assert.assertEquals("select * from " + DATA_DB + ".records", boundSql.getSql());
        deny(() -> guarded.prepare(connection("neatlogic"), null));
        Assert.assertEquals(1, prepared.get());
    }

    /** 写操作即便借用查询执行器也应在进入底层前拒绝。 */
    @Test
    public void shouldDenyUpdateAndCursorAcrossSchemas() {
        AtomicInteger executed = new AtomicInteger();
        Executor executor = guardedExecutor(connection(DB), executed, new ArrayList<>());
        deny(() -> executor.update(statement("select 1"), null));
        deny(() -> executor.queryCursor(statement("select * from neatlogic.mongodb"), null, RowBounds.DEFAULT));
        Assert.assertEquals(0, executed.get());
    }

    /** 实际 SqlRunner 调用链必须拆除 MyBatis 包装，避免异常携带 SQL 原文。 */
    @Test
    public void shouldReturnSanitizedExceptionFromRealRunner() {
        AtomicInteger prepared = new AtomicInteger();
        DataSource custom = runnableSource(DB, prepared);
        SqlInfo info = new SqlInfo();
        info.setId("query");
        info.setNamespace("fixture.security");
        for (String sql : Arrays.asList("select * from neatlogic.mongodb",
                "select ascii(substr(password,1,1))--0 from neatlogic.datasource")) {
            SqlRunner runner = new SqlRunner("<mapper><select id='query' resultType='map'>" + sql + "</select></mapper>",
                    "fixture.security", custom);
            deny(() -> runner.runSqlById(info, Collections.emptyMap()));
            deny(() -> runner.runSqlById(info, Collections.emptyMap(), new PageRowBounds(0, 10)));
        }
        Assert.assertEquals("非法查询不得准备 JDBC 语句", 0, prepared.get());
    }

    /** 自定义源实际连到管理库时，即便 SQL 无显式库名也应在执行前拒绝。 */
    @Test
    public void shouldRejectManagementConnectionThroughRealRunner() {
        AtomicInteger prepared = new AtomicInteger();
        SqlRunner runner = new SqlRunner("<mapper><select id='query' resultType='map'>select 1</select></mapper>",
                "fixture.security", runnableSource("neatlogic", prepared));
        SqlInfo info = new SqlInfo();
        info.setId("query");
        info.setNamespace("fixture.security");
        deny(() -> runner.runSqlById(info, Collections.emptyMap()));
        deny(() -> runner.runSqlById(info, Collections.emptyMap(), new PageRowBounds(0, 10)));
        Assert.assertEquals("管理库连接不得准备 JDBC 语句", 0, prepared.get());
    }

    /** 自定义源只能作用于当前 SqlRunner，不能覆盖全局默认源。 */
    @Test
    public void shouldKeepCustomDataSourcesOnEachRunner() throws Exception {
        Field field = SqlRunner.class.getDeclaredField("defaultDataSource");
        field.setAccessible(true);
        Object original = field.get(null);
        DataSource shared = proxy(DataSource.class, (target, method, args) -> null);
        DataSource custom = proxy(DataSource.class, (target, method, args) -> null);
        try {
            new SqlRunner().setDataSource(shared);
            SqlRunner customRunner = new SqlRunner("<mapper><select id='x' resultType='map'>select 1</select></mapper>", custom);
            SqlRunner defaultRunner = new SqlRunner("<mapper><select id='x' resultType='map'>select 1</select></mapper>");
            Field configurationField = SqlRunner.class.getDeclaredField("configuration");
            configurationField.setAccessible(true);
            Assert.assertSame(custom, ((Configuration) configurationField.get(customRunner)).getEnvironment().getDataSource());
            Assert.assertSame(shared, ((Configuration) configurationField.get(defaultRunner)).getEnvironment().getDataSource());
            Assert.assertSame(shared, field.get(null));
        } finally {
            field.set(null, original);
        }
    }

    /** 创建只包含库名元数据的测试池。 */
    private NeatLogicBasicDataSource source(String catalog) {
        // 本用例只读取池元数据，禁用预填充与首连校验以保持离线。
        HikariConfig config = new HikariConfig();
        config.setMinimumIdle(0);
        config.setInitializationFailTimeout(-1);
        config.setCatalog(catalog);
        config.setJdbcUrl("jdbc:mysql://fixture.invalid:3306/" + catalog);
        NeatLogicBasicDataSource source = new NeatLogicBasicDataSource(config);
        createdSources.add(source);
        return source;
    }

    /** 恢复测试前的单个路由。 */
    private void restoreRoute(String key, Object value) {
        if (value == null) {
            routes.remove(key);
        } else {
            routes.put(key, value);
        }
    }

    /** 仅提供当前库名，任何准备 SQL 的调用都会令测试失败。 */
    private Connection connection(String catalog) {
        return proxy(Connection.class, (target, method, args) -> {
            if ("getCatalog".equals(method.getName())) {
                return catalog;
            }
            if (method.getName().startsWith("prepare") || "createStatement".equals(method.getName())) {
                throw new AssertionError("测试不得访问数据库");
            }
            return null;
        });
    }

    /** 为真实 MyBatis 会话提供仅支持连接生命周期的替身，禁止准备任何 SQL。 */
    private DataSource runnableSource(String catalog, AtomicInteger prepared) {
        Connection connection = proxy(Connection.class, (target, method, args) -> {
            if ("getCatalog".equals(method.getName())) {
                return catalog;
            }
            if ("getAutoCommit".equals(method.getName())) {
                return true;
            }
            if ("isClosed".equals(method.getName())) {
                return false;
            }
            if (method.getName().startsWith("prepare") || "createStatement".equals(method.getName())) {
                prepared.incrementAndGet();
                throw new AssertionError("被拒绝的 SQL 不能进入 JDBC");
            }
            return null;
        });
        return proxy(DataSource.class, (target, method, args) -> {
            if ("getConnection".equals(method.getName())) {
                return connection;
            }
            return null;
        });
    }

    /** 包装模拟缓存执行器，计数记录能否到达底层查询。 */
    private Executor guardedExecutor(Connection connection, AtomicInteger executed, List<String> sqlList) {
        Transaction transaction = proxy(Transaction.class, (target, method, args) -> {
            if ("getConnection".equals(method.getName())) {
                return connection;
            }
            return null;
        });
        Executor delegate = proxy(Executor.class, (target, method, args) -> {
            if ("getTransaction".equals(method.getName())) {
                return transaction;
            }
            if ("createCacheKey".equals(method.getName())) {
                CacheKey key = new CacheKey();
                key.update(((BoundSql) args[3]).getSql());
                return key;
            }
            if ("query".equals(method.getName())) {
                executed.incrementAndGet();
                BoundSql boundSql = (BoundSql) args[5];
                sqlList.add(boundSql.getSql());
                return Collections.singletonList(1);
            }
            if ("update".equals(method.getName()) || "queryCursor".equals(method.getName())) {
                executed.incrementAndGet();
            }
            return null;
        });
        return (Executor) new SqlRunnerAccessInterceptor().plugin(delegate);
    }

    /** 为静态查询构造最小 MyBatis 元数据。 */
    private MappedStatement statement(String sql) {
        Configuration configuration = new Configuration();
        return new MappedStatement.Builder(configuration, "fixture.query", new StaticSqlSource(configuration, sql), SqlCommandType.SELECT)
                .resultMaps(Collections.singletonList(new ResultMap.Builder(configuration, "fixture.result", Map.class, Collections.emptyList()).build()))
                .build();
    }

    /** 生成无外部资源的接口替身。 */
    private <T> T proxy(Class<T> type, InvocationHandler handler) {
        return type.cast(Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[]{type}, (target, method, args) -> {
            if (method.getDeclaringClass() == Object.class) {
                if ("hashCode".equals(method.getName())) {
                    return System.identityHashCode(target);
                }
                if ("equals".equals(method.getName())) {
                    return target == args[0];
                }
                return "SqlRunnerTestDouble:" + type.getSimpleName();
            }
            return handler.invoke(target, method, args);
        }));
    }

    /** 确认统一异常不泄漏 SQL、配置字段或解析器错误。 */
    private void deny(CheckedAction action) {
        try {
            action.run();
            Assert.fail("应拒绝越界或不受支持的报表 SQL");
        } catch (SqlRunnerAccessDeniedException expected) {
            Assert.assertNull(expected.getCause());
            Assert.assertFalse(expected.getMessage().contains("select "));
            Assert.assertFalse(expected.getMessage().contains("password"));
        } catch (Exception unexpected) {
            throw new AssertionError("应返回统一安全异常", unexpected);
        }
    }

    /** 允许测试数据库接口上的受检异常。 */
    private interface CheckedAction {
        void run() throws Exception;
    }
}
