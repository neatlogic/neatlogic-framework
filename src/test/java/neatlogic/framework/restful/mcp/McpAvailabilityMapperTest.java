package neatlogic.framework.restful.mcp;

import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.dao.util.CacheUtil;
import neatlogic.framework.restful.dao.mapper.ApiMapper;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.executor.BaseExecutor;
import org.apache.ibatis.executor.BatchResult;
import org.apache.ibatis.executor.CachingExecutor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.*;
import org.mybatis.spring.SqlSessionHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.junit.Assert;
import org.junit.Test;

import java.io.InputStream;
import java.lang.reflect.Proxy;
import java.util.*;

/** 使用真实 Mapper、MyBatis 两级缓存及既有 CacheUtil 验证失效边界，不连接业务数据库。 */
public class McpAvailabilityMapperTest {
    /** 加载真实配置，避免测试复制 SQL 或缓存选项。 */
    private Configuration configuration() throws Exception {
        Configuration configuration = new Configuration();
        String resource = "neatlogic/framework/restful/dao/mapper/ApiMapper.xml";
        try (InputStream stream = getClass().getClassLoader().getResourceAsStream(resource)) {
            Assert.assertNotNull(stream);
            new XMLMapperBuilder(stream, configuration, resource, configuration.getSqlFragments()).parse();
        }
        return configuration;
    }

    /** 关闭记录仍交给解析器处理，实时查询既不使用也不清除 API 二级缓存。 */
    @Test public void readsDisabledOverridesWithoutCachedAvailability() throws Exception {
        Configuration configuration = configuration();
        for (String method : List.of("getMcpApiListByModuleId", "getApiListByTokenList")) {
            MappedStatement statement = configuration.getMappedStatement(ApiMapper.class.getName() + "." + method);
            Assert.assertFalse(statement.isUseCache());
            Assert.assertFalse(statement.isFlushCacheRequired());
        }
        BoundSql bound = configuration.getMappedStatement(ApiMapper.class.getName() + ".getMcpApiListByModuleId")
                .getBoundSql(Map.of("moduleIdList", new ArrayList<>(List.of("ai", "monitor"))));
        String sql = bound.getSql().replaceAll("\\s+", " ").toLowerCase(Locale.ROOT);
        String conditions = sql.substring(sql.indexOf("where"));
        Assert.assertFalse(conditions.contains("is_mcp"));
        Assert.assertFalse(conditions.contains("is_active"));
        Assert.assertTrue(conditions.contains("module_id"));
        Assert.assertEquals(2, bound.getParameterMappings().size());
        Assert.assertFalse(sql.contains("password"));
        Assert.assertFalse(sql.contains("`config`"));
    }

    /** 同一会话内启停可刷新，同时已经缓存的普通 API 查询继续命中二级缓存。 */
    @Test public void refreshesLocalAvailabilityWithoutEvictingOtherApiEntries() throws Exception {
        TenantContext previous = TenantContext.get();
        TenantContext.init("mcp-cache-regression");
        Object resource = new Object();
        TransactionSynchronizationManager.initSynchronization();
        try {
            Configuration configuration = configuration();
            MappedStatement normal = configuration.getMappedStatement(ApiMapper.class.getName() + ".getApiByToken");
            MappedStatement live = configuration.getMappedStatement(ApiMapper.class.getName() + ".getMcpApiListByModuleId");
            normal.getCache().clear();
            MemoryExecutor database = new MemoryExecutor(configuration);
            CachingExecutor executor = new CachingExecutor(database);
            SqlSession session = (SqlSession) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{SqlSession.class}, (proxy, method, args) -> {
                if (!"clearCache".equals(method.getName())) throw new AssertionError(method.getName());
                executor.clearLocalCache(); return null;
            });
            TransactionSynchronizationManager.bindResource(resource, new SqlSessionHolder(session, ExecutorType.SIMPLE, null));
            executor.query(normal, "normal-api", RowBounds.DEFAULT, null); executor.commit(false);
            executor.query(normal, "normal-api", RowBounds.DEFAULT, null);
            Assert.assertEquals(1, database.reads);
            Map<String, Object> params = Map.of("moduleIdList", new ArrayList<>(List.of("ai")));
            Assert.assertEquals(List.of(1), executor.query(live, params, RowBounds.DEFAULT, null));
            database.enabled = 0;
            Assert.assertEquals(List.of(1), executor.query(live, params, RowBounds.DEFAULT, null));
            CacheUtil.clearSqlSessionCache();
            Assert.assertEquals(List.of(0), executor.query(live, params, RowBounds.DEFAULT, null));
            database.enabled = 1;
            CacheUtil.clearSqlSessionCache();
            Assert.assertEquals(List.of(1), executor.query(live, params, RowBounds.DEFAULT, null));
            executor.commit(false);
            int reads = database.reads;
            executor.query(normal, "normal-api", RowBounds.DEFAULT, null);
            Assert.assertEquals(reads, database.reads);
            normal.getCache().clear();
        } finally {
            TransactionSynchronizationManager.unbindResourceIfPossible(resource);
            TransactionSynchronizationManager.clearSynchronization();
            TenantContext.init(previous);
        }
    }

    /** 仅替换数据库读取，一级缓存及二级缓存仍运行真实 MyBatis 实现。 */
    private static class MemoryExecutor extends BaseExecutor {
        private int reads;
        private int enabled = 1;
        /** 不建立数据库连接。 */
        private MemoryExecutor(Configuration configuration) { super(configuration, null); }
        /** 模拟数据库返回当时的开关快照。 */
        @Override @SuppressWarnings("unchecked") protected <E> List<E> doQuery(MappedStatement statement, Object parameter, RowBounds bounds, ResultHandler handler, BoundSql sql) {
            reads++; return (List<E>) List.of(enabled);
        }
        /** 诊断用例禁止写数据库。 */
        @Override protected int doUpdate(MappedStatement statement, Object parameter) { throw new AssertionError("禁止写入"); }
        /** 诊断用例无批量写入。 */
        @Override protected List<BatchResult> doFlushStatements(boolean rollback) { return List.of(); }
        /** 诊断用例不使用游标。 */
        @Override protected <E> Cursor<E> doQueryCursor(MappedStatement statement, Object parameter, RowBounds bounds, BoundSql sql) { throw new AssertionError("禁止游标"); }
    }
}
