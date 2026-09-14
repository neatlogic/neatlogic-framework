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

import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.asynchronization.threadlocal.UserContext;
import neatlogic.framework.globallock.dao.mapper.GlobalLockOperationMapper;
import com.zaxxer.hikari.HikariDataSource;
import neatlogic.framework.dto.globallock.GlobalLockVo;
import neatlogic.framework.globallock.core.*;
import neatlogic.framework.globallock.dao.mapper.GlobalLockMapper;
import neatlogic.framework.transaction.util.TransactionUtil;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.*;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.transaction.SpringManagedTransactionFactory;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionStatus;
import java.io.*;
import java.lang.reflect.Field;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.function.IntConsumer;
import javax.sql.DataSource;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import static org.junit.Assert.*;

/** 仅连接显式指定的空白 lock_it_ 测试库，执行实际资源锁 Mapper 和 InnoDB 事务。 */
public class GlobalLockMysqlIT {
    private static final Logger logger = LoggerFactory.getLogger(GlobalLockMysqlIT.class);
    private HikariDataSource source;
    private DataSource transactionSource;
    private JdbcTemplate jdbc;
    private GlobalLockMapper mapper;
    private GlobalLockOperationMapper operationMapper;
    private Map<String, IGlobalLockHandler> handlers;
    private IGlobalLockHandler previous;
    private String sharedSql;
    private GlobalLockI18nFixture i18n;

    /** 环境缺失时明确跳过，拒绝在非测试库或已有业务表的库中运行。 */
    @Before public void setup() throws Exception {
        String url = System.getenv("LOCK_MYSQL_TEST_URL");
        Assume.assumeTrue("未配置隔离 MySQL 测试库", url != null);
        assertTrue("仅允许 lock_it_ 前缀测试库", url.matches("jdbc:mysql://[^/]+/lock_it_[A-Za-z0-9_]+(\\?.*)?"));
        i18n = new GlobalLockI18nFixture();
        source = new HikariDataSource(); source.setJdbcUrl(url);
        source.setUsername(System.getenv("LOCK_MYSQL_TEST_USER")); source.setPassword(System.getenv("LOCK_MYSQL_TEST_PASSWORD"));
        source.setMaximumPoolSize(128); source.setMinimumIdle(0);
        transactionSource = Boolean.getBoolean("lock.profile") ? new GlobalLockProfileDataSource(source) : source;
        jdbc = new JdbcTemplate(transactionSource);
        assertEquals(Integer.valueOf(0), jdbc.queryForObject("SELECT COUNT(*) FROM information_schema.tables WHERE table_schema=DATABASE()", Integer.class));
        jdbc.execute("CREATE TABLE global_lock_pk(lock_uuid CHAR(32) PRIMARY KEY) ENGINE=InnoDB");
        jdbc.execute("CREATE TABLE global_lock(id BIGINT PRIMARY KEY,uuid CHAR(32),`key` VARCHAR(512),handler VARCHAR(64),handler_param LONGTEXT,owner_id VARCHAR(128) NULL,is_lock INT,description TEXT,fcd DATETIME(3),notify_error LONGTEXT,unlock_error LONGTEXT,notify_revision BIGINT NOT NULL DEFAULT 0,unlock_revision BIGINT NOT NULL DEFAULT 0,INDEX idx_uuid(uuid),INDEX idx_handler_owner_uuid(handler,owner_id,uuid)) ENGINE=InnoDB");
        String changelog=new String(Files.readAllBytes(Paths.get("src/main/resources/neatlogic/resources/framework/changelog/2026-09-11/neatlogic_tenant.sql")),StandardCharsets.UTF_8);
        int operationStart=changelog.indexOf("CREATE TABLE");
        jdbc.execute(changelog.substring(operationStart,changelog.indexOf(';',operationStart)));
        jdbc.execute("CREATE TABLE autoexec_job(id BIGINT PRIMARY KEY,status VARCHAR(32)) ENGINE=InnoDB");
        jdbc.update("INSERT INTO autoexec_job VALUES(1,'running'),(2,'running')");
        new TransactionUtil(new DataSourceTransactionManager(transactionSource));
        Configuration config = new Configuration(new Environment("lock-test", new SpringManagedTransactionFactory(), transactionSource));
        try (InputStream xml = Files.newInputStream(Paths.get("src/main/java/neatlogic/framework/globallock/dao/mapper/GlobalLockMapper.xml"))) {
            new XMLMapperBuilder(xml, config, "GlobalLockMapper.xml", config.getSqlFragments()).parse();
        }
        try (InputStream operationXml = Files.newInputStream(Paths.get("src/main/java/neatlogic/framework/globallock/dao/mapper/GlobalLockOperationMapper.xml"))) {
            new XMLMapperBuilder(operationXml, config, "GlobalLockOperationMapper.xml", config.getSqlFragments()).parse();
        }
        SqlSessionTemplate session=new SqlSessionTemplate(new SqlSessionFactoryBuilder().build(config));
        mapper = session.getMapper(GlobalLockMapper.class);
        operationMapper = session.getMapper(GlobalLockOperationMapper.class);
        // 直接读取生产 Mapper 中的独立主键共享锁 SQL，避免测试另写一套锁语义。
        String xml = new String(Files.readAllBytes(Paths.get("../neatlogic-autoexec-base/src/main/java/neatlogic/framework/autoexec/dao/mapper/AutoexecJobMapper.xml")), StandardCharsets.UTF_8);
        int start = xml.indexOf("<select id=\"getJobStatusForShare\"");
        sharedSql = xml.substring(xml.indexOf('>', start)+1, xml.indexOf("</select>", start)).replace("#{value}", "?").trim();
        new GlobalLockManager(mapper).setOperations(new GlobalLockOperationManager());
        Field field = GlobalLockHandlerFactory.class.getDeclaredField("handlerMap"); field.setAccessible(true);
        handlers = (Map<String, IGlobalLockHandler>) field.get(null); previous = handlers.put("deploy", new Handler());
    }

    /** 只删除本测试创建的表；初始化未完成时不触碰已有表。 */
    @After public void cleanup() throws Exception {
        if (handlers != null) {
            if (previous == null) handlers.remove("deploy"); else handlers.put("deploy", previous);
            for (String table : Arrays.asList("global_lock_operation", "global_lock", "global_lock_pk", "autoexec_job")) jdbc.execute("DROP TABLE " + table);
        }
        if (source != null) source.close();
        if (i18n != null) i18n.close();
    }

    /** 同资源写竞争、读共享和独立资源在三个并发规模下均验证最终持有者。 */
    @Test public void contentionAndIndependentResources() throws Exception {
        for (int concurrency : new int[]{16,64,128}) {
            for (String mode : new String[]{"write","read","independent","independentJobs"}) {
                jdbc.update("DELETE FROM global_lock"); jdbc.update("DELETE FROM global_lock_pk");
                for (int job=1;job<=concurrency;job++) jdbc.update("INSERT IGNORE INTO autoexec_job VALUES(?,'running')",job);
                List<Long> elapsed = parallel(concurrency, index -> {
                    long jobId="independentJobs".equals(mode)?index+1:1;
                    GlobalLockVo lock = request(index + 1, mode.startsWith("independent") ? "resource/"+index : "resource", "read".equals(mode) ? "read" : "write",jobId);
                    GlobalLockManager.getLock(lock);
                    GlobalLockManager.retryLock(request(index + 1, lock.getKey(), "read".equals(mode) ? "read" : "write",jobId));
                });
                int expected = "write".equals(mode) ? 1 : concurrency;
                assertEquals(Integer.valueOf(expected), jdbc.queryForObject("SELECT COUNT(*) FROM global_lock WHERE is_lock=1", Integer.class));
                assertEquals(Integer.valueOf(concurrency), jdbc.queryForObject("SELECT COUNT(*) FROM global_lock", Integer.class));
                Collections.sort(elapsed);
                logger.info("MySQL lock result concurrency={}, mode={}, p95Ms={}, p99Ms={}",concurrency,mode,elapsed.get((int)Math.ceil(concurrency*.95)-1)/1e6,elapsed.get((int)Math.ceil(concurrency*.99)-1)/1e6);
            }
        }
    }

    /** 相同 ID 并发重试不会新增业务记录，终态提交后拒绝迟到重建。 */
    @Test public void duplicateRetryAndTerminalRejection() throws Exception {
        parallel(128, index -> GlobalLockManager.retryLock(request(1,"resource","write")));
        assertEquals(Integer.valueOf(1),jdbc.queryForObject("SELECT COUNT(*) FROM global_lock",Integer.class));
        GlobalLockTransaction.execute(() -> {
            jdbc.update("UPDATE autoexec_job SET status='completed' WHERE id=1");
            GlobalLockManager.releaseOwnedLocks(Collections.singletonList("deploy"), "1");
        });
        assertEquals(Integer.valueOf(0),jdbc.queryForObject("SELECT COUNT(*) FROM global_lock",Integer.class));
        try { GlobalLockManager.retryLock(request(1,"resource","write")); fail("terminal request accepted"); }
        catch (DataIntegrityViolationException expected) { logger.error("Expected terminal rejection",expected); }
        assertEquals(Integer.valueOf(0),jdbc.queryForObject("SELECT COUNT(*) FROM global_lock",Integer.class));
    }

    /** 主动构造两行反向排他锁，验证真实死锁后整笔事务重新执行。 */
    @Test public void realDeadlockRestartsTransaction() throws Exception {
        CyclicBarrier barrier = new CyclicBarrier(2); AtomicInteger attempts = new AtomicInteger();
        parallel(2, index -> {
            AtomicInteger local = new AtomicInteger();
            GlobalLockTransaction.execute(() -> {
                attempts.incrementAndGet();
                jdbc.queryForList("SELECT id FROM autoexec_job WHERE id=? FOR UPDATE",index+1);
                if (local.getAndIncrement()==0) await(barrier);
                jdbc.queryForList("SELECT id FROM autoexec_job WHERE id=? FOR UPDATE",2-index);
            });
        });
        assertTrue(attempts.get() >= 3);
    }

    /** 共享状态锁同时持有时不应形成同一作业的排他串行。 */
    @Test public void jobSharedLocksOverlap() throws Exception {
        // 先准备足够连接，屏障只检验共享锁重叠，不把连接池扩容耗时混入判定。
        List<Connection> connections = new ArrayList<>();
        try { for (int i=0;i<16;i++) connections.add(source.getConnection()); }
        finally { for (Connection connection : connections) connection.close(); }
        CyclicBarrier barrier = new CyclicBarrier(16);
        parallel(16,index -> GlobalLockTransaction.execute(() -> {
            jdbc.queryForList(sharedSql,1); await(barrier);
        }));
    }

    /** 取消提交先于获锁时，取得协调行后必须识别记录不存在。 */
    @Test public void cancelBeforeAcquisitionDoesNotReportHeld() throws Exception {
        GlobalLockVo lock=request(1,"resource","write"); GlobalLockManager.insertLock(lock);
        TransactionStatus cancelling=TransactionUtil.openTx();
        mapper.getGlobalLockPkByUuidForUpdate(lock.getUuid()); mapper.deleteLock(1L);
        ExecutorService executor=Executors.newSingleThreadExecutor();
        try {
            Future<GlobalLockVo> result=executor.submit(() -> {
                Method acquire=GlobalLockManager.class.getDeclaredMethod("lock",GlobalLockVo.class);
                acquire.setAccessible(true); return (GlobalLockVo)acquire.invoke(null,lock);
            });
            TransactionUtil.commitTx(cancelling);
            assertEquals(Integer.valueOf(0),result.get(10,TimeUnit.SECONDS).getIsLock());
        } finally {
            if (!cancelling.isCompleted()) TransactionUtil.rollbackTx(cancelling);
            executor.shutdownNow();
        }
    }

    /** 真实锁等待超时后重新开启事务，不能在原事务中继续执行。 */
    @Test public void realLockTimeoutRetries() throws Exception {
        jdbc.update("INSERT INTO global_lock_pk VALUES('blocked')");
        AtomicInteger attempts=new AtomicInteger();
        try (Connection blocker=source.getConnection()) {
            blocker.setAutoCommit(false);
            blocker.createStatement().executeQuery("SELECT * FROM global_lock_pk WHERE lock_uuid='blocked' FOR UPDATE").close();
            CountDownLatch firstTimeout = new CountDownLatch(1);
            ExecutorService releaser = Executors.newSingleThreadExecutor();
            Future<?> released = releaser.submit(() -> {
                try {
                    if (!firstTimeout.await(15, TimeUnit.SECONDS)) throw new AssertionError("未触发真实锁超时");
                    blocker.rollback();
                } catch (Exception ex) { logger.error("Release test blocker failed", ex); throw new AssertionError(ex); }
            });
            try {
                GlobalLockTransaction.execute(() -> {
                    attempts.incrementAndGet(); jdbc.execute("SET SESSION innodb_lock_wait_timeout=1");
                    try { mapper.getGlobalLockPkByUuidForUpdate("blocked"); }
                    catch (RuntimeException ex) {
                        logger.error("Expected injected lock timeout", ex);
                        if (GlobalLockTransaction.isLockConflict(ex)) firstTimeout.countDown();
                        throw ex;
                    }
                });
                released.get(15, TimeUnit.SECONDS);
                assertTrue(attempts.get()>=2);
            } finally { releaser.shutdownNow(); blocker.rollback(); }
        }
    }

    /** 外层事务遇到真实超时只标记回滚，不局部重试或提交已有写入。 */
    @Test public void outerTransactionTimeoutDoesNotRetry() throws Exception {
        jdbc.update("INSERT INTO global_lock_pk VALUES('blocked')");
        try (Connection blocker=source.getConnection()) {
            blocker.setAutoCommit(false);
            blocker.createStatement().executeQuery("SELECT * FROM global_lock_pk WHERE lock_uuid='blocked' FOR UPDATE").close();
            TransactionStatus outer=TransactionUtil.openTx(); AtomicInteger attempts=new AtomicInteger();
            try {
                jdbc.update("INSERT INTO global_lock_pk VALUES('outer-write')");
                GlobalLockTransaction.execute(() -> {
                    attempts.incrementAndGet(); jdbc.execute("SET SESSION innodb_lock_wait_timeout=1");
                    mapper.getGlobalLockPkByUuidForUpdate("blocked");
                });
                fail("expected timeout");
            } catch (RuntimeException expected) {
                logger.error("Expected outer transaction timeout",expected);
                assertTrue(GlobalLockTransaction.isLockConflict(expected));
                assertTrue(outer.isRollbackOnly()); assertEquals(1,attempts.get());
            } finally { TransactionUtil.rollbackTx(outer);blocker.rollback(); }
        }
        assertEquals(Integer.valueOf(0),jdbc.queryForObject("SELECT COUNT(*) FROM global_lock_pk WHERE lock_uuid='outer-write'",Integer.class));
    }

    /** 状态更新与申请同时到达，终态提交后的所有请求都不能留下记录。 */
    @Test public void terminalRacesWithAcquisition() throws Exception {
        parallel(64,index -> {
            if (index==0) {
                GlobalLockTransaction.execute(() -> {
                    jdbc.update("UPDATE autoexec_job SET status='completed' WHERE id=1");
                    GlobalLockManager.releaseOwnedLocks(Collections.singletonList("deploy"), "1");
                });
            } else {
                try { GlobalLockManager.getLock(request(index,"resource/"+index,"write")); }
                catch (DataIntegrityViolationException expected) { logger.error("Expected terminal rejection",expected); }
            }
        });
        assertEquals(Integer.valueOf(0),jdbc.queryForObject("SELECT COUNT(*) FROM global_lock",Integer.class));
    }

    /** 数据库分配的新通知顺序覆盖旧尝试，失败通知不回滚删除。 */
    @Test public void failedNotificationAndRevisionOrdering() {
        GlobalLockManager.getLock(request(1,"resource","write"));
        GlobalLockManager.getLock(request(2,"resource","write"));
        handlers.put("deploy",new Handler() {
            @Override protected void myDoNotify(GlobalLockVo lock,JSONObject param) {
                // 独立连接确认删除已提交，模拟失效等待进程拒绝连接。
                try (Connection connection=source.getConnection(); Statement statement=connection.createStatement()) {
                    ResultSet result=statement.executeQuery("SELECT COUNT(*) FROM global_lock WHERE id=1");
                    result.next(); assertEquals(0,result.getInt(1));
                } catch (SQLException ex) { logger.error("Verify committed release failed",ex); throw new AssertionError(ex); }
                throw new DataAccessResourceFailureException("errno=111");
            }
        });
        GlobalLockManager.unLock(1L,new JSONObject());
        assertNull(mapper.getGlobalLockById(1L)); assertNotNull(mapper.getGlobalLockById(2L).getNotifyError());
        GlobalLockTransaction.execute(() -> { mapper.incrementNotifyRevision(2L); });
        Long older=mapper.getNotifyRevision(2L);
        GlobalLockTransaction.execute(() -> { mapper.incrementNotifyRevision(2L); });
        Long newer=mapper.getNotifyRevision(2L);
        mapper.updateLockError(2L,true,null,newer); mapper.updateLockError(2L,true,"old failure",older);
        assertNull(mapper.getGlobalLockById(2L).getNotifyError());
    }

    /** 两个 Web 服务实例竞争同一操作，数据库只能授予一次执行权。 */
    @Test public void multipleWebInstancesClaimOnlyOnce() throws Exception {
        GlobalLockOperationManager first=operationManager(),second=operationManager();
        UserContext.init((UserContext)null).setUserUuid("tester");
        String id;
        try { id=first.prepare(1L,"unlock").getString("operationId"); }
        finally { UserContext.get().release(); }
        AtomicInteger claims=new AtomicInteger();
        parallel(128,index -> {
            UserContext.init((UserContext)null).setUserUuid("tester");
            try { if ((index%2==0?first:second).claim(id,1L,"unlock")) claims.incrementAndGet(); }
            finally { UserContext.get().release(); }
        });
        assertEquals(1,claims.get());
    }

    /** 慢通知期间删除和进度已提交，其他事务仍能取得同一 UUID 行锁。 */
    @Test public void slowNotificationDoesNotHoldDatabaseLocks() throws Exception {
        GlobalLockOperationManager operations=operationManager();
        new GlobalLockManager(mapper).setOperations(operations);
        GlobalLockManager.getLock(request(1,"resource","write"));
        GlobalLockManager.getLock(request(2,"resource","write"));
        CountDownLatch notifying=new CountDownLatch(1),complete=new CountDownLatch(1);
        handlers.put("deploy",new Handler() {
            @Override protected void myDoNotify(GlobalLockVo lock,JSONObject param) {
                notifying.countDown();
                try { assertTrue(complete.await(10,TimeUnit.SECONDS)); }
                catch (InterruptedException ex) { logger.error("Slow notification interrupted",ex); Thread.currentThread().interrupt(); throw new AssertionError(ex); }
            }
        });
        UserContext.init((UserContext)null).setUserUuid("tester");
        ExecutorService executor=Executors.newSingleThreadExecutor();
        try {
            JSONObject operation=operations.prepare(1L,"unlock"); String id=operation.getString("operationId");
            assertTrue(operations.claim(id,1L,"unlock"));
            Future<?> result=executor.submit(() -> {
                UserContext.init((UserContext)null).setUserUuid("tester");
                try { JSONObject param=new JSONObject();param.put("operationId",id);GlobalLockManager.release(1L,param,true); }
                finally { UserContext.get().release(); }
            });
            assertTrue(notifying.await(10,TimeUnit.SECONDS));
            assertNull(mapper.getGlobalLockById(1L));
            assertEquals("released",operations.get(id).getString("releaseStatus"));
            GlobalLockTransaction.execute(() -> {
                jdbc.execute("SET SESSION innodb_lock_wait_timeout=1");
                mapper.getGlobalLockPkByUuidForUpdate(request(1,"resource","write").getUuid());
            });
            complete.countDown(); result.get(10,TimeUnit.SECONDS);
            assertEquals("done",operations.get(id).getString("state"));
        } finally { complete.countDown();executor.shutdownNow();UserContext.get().release(); }
    }

    /** 注入同一真实 Mapper，模拟不同 Web 实例共享数据库。 */
    private GlobalLockOperationManager operationManager() throws Exception {
        GlobalLockOperationManager manager=new GlobalLockOperationManager();
        Field field=GlobalLockOperationManager.class.getDeclaredField("mapper");field.setAccessible(true);field.set(manager,operationMapper);
        return manager;
    }

    /** 同一数据库交替执行原分支与当前实现，测量完整申请及取消周期。 */
    @Test public void benchmarkAgainstBranchBaseline() throws Exception {
        Assume.assumeTrue("性能压测单独运行，避免与功能测试交错", Boolean.getBoolean("lock.benchmark"));
        Configuration baselineConfig = new Configuration(new Environment("baseline", new SpringManagedTransactionFactory(), transactionSource));
        try (InputStream xml = Files.newInputStream(Paths.get("src/test/resources/globallock/baseline-mapper.xml"))) {
            new XMLMapperBuilder(xml, baselineConfig, "baseline-mapper.xml", baselineConfig.getSqlFragments()).parse();
        }
        GlobalLockMapper baselineMapper = new SqlSessionTemplate(new SqlSessionFactoryBuilder().build(baselineConfig)).getMapper(GlobalLockMapper.class);
        new GlobalLockBaselineManager(baselineMapper);
        List<String> report = new ArrayList<>();
        report.add("scope,concurrency,round,variant,operations,failures,seconds,throughput,p95_ms,p99_ms,server_deadlocks,server_row_lock_waits,server_row_lock_time_ms");
        for (String scope : new String[]{"generic", "job"}) {
            handlers.put("deploy", "generic".equals(scope) ? new Handler() {
                /** 通用锁基线不附加作业业务校验。 */
                @Override public void validateAcquisition(GlobalLockVo lock) { }
            } : new Handler());
            for (int concurrency : new int[]{16,64,128}) {
                for (int job=1;job<=concurrency;job++) jdbc.update("INSERT IGNORE INTO autoexec_job VALUES(?,'running')",job);
                benchmarkRound(concurrency,30,false); benchmarkRound(concurrency,30,true);
                for (int round=0;round<4;round++) {
                    for (boolean revised : (round%2==0 ? new boolean[]{false,true} : new boolean[]{true,false})) {
                        String line=scope+","+concurrency+","+round+","+(revised?"revised":"baseline")+","+benchmarkRound(concurrency,100,revised);
                        report.add(line); logger.info("BENCHMARK {}",line);
                        Files.write(Paths.get("target/lock-mysql-benchmark.csv"),report,StandardCharsets.UTF_8);
                    }
                }
            }
        }
    }

    /** 固定每线程独立资源，预热之后测量，保留失败率而非只统计成功样本。 */
    private String benchmarkRound(int concurrency,int cycles,boolean revised) throws Exception {
        List<Long> samples=Collections.synchronizedList(new ArrayList<>());
        AtomicInteger failures=new AtomicInteger();
        long deadlocks=serverCounter("Innodb_deadlocks"),waits=serverCounter("Innodb_row_lock_waits"),waitTime=serverCounter("Innodb_row_lock_time");
        long started=System.nanoTime();
        parallel(concurrency,index -> {
            for (int cycle=0;cycle<cycles;cycle++) {
                long time=System.nanoTime();
                GlobalLockVo lock=request(1000000L+index,"benchmark/"+index,"write",index+1);
                try {
                    if (revised) GlobalLockManager.getLock(lock); else GlobalLockBaselineManager.getLock(lock);
                    if (lock.getIsLock()!=1) failures.incrementAndGet();
                    if (revised) GlobalLockManager.cancelLock(lock.getId()); else GlobalLockBaselineManager.cancelLock(lock.getId());
                } catch (RuntimeException ex) { logger.error("Benchmark request failed",ex); failures.incrementAndGet(); }
                samples.add(System.nanoTime()-time);
            }
        });
        double seconds=(System.nanoTime()-started)/1e9; Collections.sort(samples);
        return String.format(Locale.ROOT,"%d,%d,%.4f,%.2f,%.3f,%.3f,%d,%d,%d",samples.size(),failures.get(),seconds,(samples.size()-failures.get())/seconds,
                samples.get((int)Math.ceil(samples.size()*.95)-1)/1e6,samples.get((int)Math.ceil(samples.size()*.99)-1)/1e6,
                serverCounter("Innodb_deadlocks")-deadlocks,serverCounter("Innodb_row_lock_waits")-waits,serverCounter("Innodb_row_lock_time")-waitTime);
    }

    /** 只分解 16 并发的完整周期，反射调用相同私有获锁方法，不改生产实现。 */
    @Test public void profileSixteenConnections() throws Exception {
        Assume.assumeTrue("分阶段采样独立运行", Boolean.getBoolean("lock.profile"));
        Configuration baselineConfig = new Configuration(new Environment("profile-baseline", new SpringManagedTransactionFactory(), transactionSource));
        try (InputStream xml = Files.newInputStream(Paths.get("src/test/resources/globallock/baseline-mapper.xml"))) {
            new XMLMapperBuilder(xml, baselineConfig, "baseline-mapper.xml", baselineConfig.getSqlFragments()).parse();
        }
        new GlobalLockBaselineManager(new SqlSessionTemplate(new SqlSessionFactoryBuilder().build(baselineConfig)).getMapper(GlobalLockMapper.class));
        Method oldAcquire = GlobalLockBaselineManager.class.getDeclaredMethod("lock", GlobalLockVo.class);
        Method newAcquire = GlobalLockManager.class.getDeclaredMethod("lock", GlobalLockVo.class);
        oldAcquire.setAccessible(true); newAcquire.setAccessible(true);
        for (int job=1;job<=16;job++) jdbc.update("INSERT IGNORE INTO autoexec_job VALUES(?,'running')",job);
        List<String> rows = Collections.synchronizedList(new ArrayList<>());
        List<String> windows = new ArrayList<>(); windows.add("scope,round,variant,start_ms,end_ms");
        StringBuilder header = new StringBuilder("scope,round,variant,total_ns");
        for (String phase : new String[]{"enqueue", "acquire", "cancel"}) {
            header.append(',').append(phase).append("_ns");
            for (String category : new String[]{"connection", "execute", "commit", "config", "jdbc_other"}) header.append(',').append(phase).append('_').append(category).append("_ns,").append(phase).append('_').append(category).append("_count");
        }
        rows.add(header.toString()); GlobalLockProfileDataSource.SQL.clear();
        Files.write(Paths.get("target/lock-profile-server.csv"),
                Collections.singletonList("scope,round,variant,kind,event,calls,total_ps,lock_ps"), StandardCharsets.UTF_8);
        for (String scope : new String[]{"generic", "job"}) {
            handlers.put("deploy", "generic".equals(scope) ? new Handler() {
                /** 通用框架样本不附加作业约束。 */
                @Override public void validateAcquisition(GlobalLockVo lock) { }
            } : new Handler());
            // 增加预热周期，采样窗口仅包含正式样本。
            benchmarkRound(16,300,false); benchmarkRound(16,300,true);
            for (int round=0;round<4;round++) {
                for (boolean revised : round%2==0?new boolean[]{false,true}:new boolean[]{true,false}) {
                    final String label = scope+","+round+","+(revised?"revised":"baseline");
                    Map<String, long[]> serverBefore = profileServerSnapshot();
                    long begin = System.currentTimeMillis();
                    parallel(16,index -> {
                        for (int cycle=0;cycle<300;cycle++) {
                            GlobalLockProfileDataSource.Sample sample = new GlobalLockProfileDataSource.Sample(label);
                            GlobalLockProfileDataSource.CURRENT.set(sample);
                            long started = System.nanoTime();
                            try {
                                GlobalLockVo lock=request(1000000L+index,"benchmark/"+index,"write",index+1);
                                long phase=System.nanoTime();
                                if (revised) GlobalLockManager.insertLock(lock); else GlobalLockBaselineManager.insertLock(lock);
                                sample.phaseTime[0]=System.nanoTime()-phase;
                                sample.phase=1; phase=System.nanoTime();
                                if (lock.getIsLock()!=1) invokeAcquire(revised?newAcquire:oldAcquire,lock);
                                sample.phaseTime[1]=System.nanoTime()-phase;
                                assertEquals(Integer.valueOf(1),lock.getIsLock());
                                sample.phase=2; phase=System.nanoTime();
                                if (revised) GlobalLockManager.cancelLock(lock.getId()); else GlobalLockBaselineManager.cancelLock(lock.getId());
                                sample.phaseTime[2]=System.nanoTime()-phase;
                                rows.add(sample.row(System.nanoTime()-started));
                            } finally { GlobalLockProfileDataSource.CURRENT.remove(); }
                        }
                    });
                    windows.add(label+","+begin+","+System.currentTimeMillis());
                    writeServerProfile(label, serverBefore, profileServerSnapshot());
                    logger.info("PROFILE {} completed",label);
                    Files.write(Paths.get("target/lock-profile-samples.csv"),rows,StandardCharsets.UTF_8);
                    Files.write(Paths.get("target/lock-profile-windows.csv"),windows,StandardCharsets.UTF_8);
                }
            }
        }
        List<String> sql = new ArrayList<>(); sql.add("scope,round,variant,phase,sql,calls,total_ns");
        GlobalLockProfileDataSource.SQL.forEach((key,value) -> {
            String[] fields=key.split("\\|",3);
            sql.add(fields[0]+","+fields[1]+",\""+fields[2].replace("\"","\"\"")+"\","+value.calls.sum()+","+value.nanos.sum());
        });
        Files.write(Paths.get("target/lock-profile-sql.csv"),sql,StandardCharsets.UTF_8);
    }

    /** 只读取当前隔离库的语句计时和连接等待计数，不重置或开启共享服务器的采样配置。 */
    private Map<String, long[]> profileServerSnapshot() {
        Map<String, long[]> snapshot = new TreeMap<>();
        // 不同摘要哈希可能显示相同文本，先合计，防止 Map 覆盖其中一组统计。
        jdbc.query("SELECT DIGEST_TEXT, SUM(COUNT_STAR), SUM(SUM_TIMER_WAIT), SUM(SUM_LOCK_TIME) "
                + "FROM performance_schema.events_statements_summary_by_digest WHERE SCHEMA_NAME=DATABASE() "
                + "AND LOWER(DIGEST_TEXT) NOT LIKE '%performance_schema%' GROUP BY DIGEST_TEXT", result -> {
            snapshot.put("statement|"+result.getString(1), new long[]{result.getLong(2), result.getLong(3), result.getLong(4)});
        });
        jdbc.query("SELECT e.EVENT_NAME, SUM(e.COUNT_STAR), SUM(e.SUM_TIMER_WAIT) "
                + "FROM performance_schema.events_waits_summary_by_thread_by_event_name e "
                + "JOIN performance_schema.threads t ON t.THREAD_ID=e.THREAD_ID "
                + "WHERE t.PROCESSLIST_DB=DATABASE() AND e.COUNT_STAR>0 GROUP BY e.EVENT_NAME", result -> {
            snapshot.put("wait|"+result.getString(1), new long[]{result.getLong(2), result.getLong(3), 0});
        });
        return snapshot;
    }

    /** 保存窗口前后差值；服务端计时单位为皮秒，不能直接与客户端纳秒混用。 */
    private void writeServerProfile(String label, Map<String, long[]> before, Map<String, long[]> after) throws IOException {
        List<String> rows = new ArrayList<>();
        for (Map.Entry<String, long[]> entry : after.entrySet()) {
            long[] previous = before.getOrDefault(entry.getKey(), new long[3]);
            long[] current = entry.getValue();
            if (current[0] == previous[0]) continue;
            String[] key = entry.getKey().split("\\|", 2);
            rows.add(label+","+key[0]+",\""+key[1].replace("\"", "\"\"")+"\","
                    +(current[0]-previous[0])+","+(current[1]-previous[1])+","+(current[2]-previous[2]));
        }
        Files.write(Paths.get("target/lock-profile-server.csv"), rows, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }

    /** 保留原获锁异常，不通过反射包装影响测试结果。 */
    private void invokeAcquire(Method method, GlobalLockVo lock) {
        try { method.invoke(null,lock); }
        catch (ReflectiveOperationException ex) { logger.error("Profile acquisition failed",ex); throw new AssertionError(ex); }
    }

    /** 记录服务端累计计数差值；共享服务器上该值可能包含其他会话活动。 */
    private long serverCounter(String name) {
        return jdbc.query("SHOW GLOBAL STATUS LIKE ?", result -> result.next()?Long.parseLong(result.getString(2)):0L,name);
    }

    /** 统一等待起跑并传播工作线程异常，不能把部分失败计作成功。 */
    private List<Long> parallel(int count, IntConsumer work) throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(count);
        CountDownLatch start = new CountDownLatch(1); List<Future<Long>> futures = new ArrayList<>();
        try {
            for (int i=0;i<count;i++) { final int index=i; futures.add(pool.submit(() -> {
                start.await(); long time=System.nanoTime(); work.accept(index); return System.nanoTime()-time;
            })); }
            start.countDown(); List<Long> results=new ArrayList<>();
            for (Future<Long> future:futures) results.add(future.get(90,TimeUnit.SECONDS));
            return results;
        } finally { pool.shutdownNow(); pool.awaitTermination(10,TimeUnit.SECONDS); }
    }

    /** 屏障带超时，锁顺序回归时失败而非永久挂起。 */
    private void await(CyclicBarrier barrier) {
        try { barrier.await(10,TimeUnit.SECONDS); }
        catch (Exception ex) { logger.error("Concurrency barrier failed",ex); throw new AssertionError(ex); }
    }

    /** 构造固定归属请求，测试重试不会变更执行身份。 */
    private GlobalLockVo request(long id,String key,String mode) {
        return request(id,key,mode,1);
    }

    /** 指定作业归属，覆盖不同作业的无竞争资源申请。 */
    private GlobalLockVo request(long id,String key,String mode,long jobId) {
        return new GlobalLockVo(id,"deploy",key,"{\"jobId\":"+jobId+",\"execId\":1,\"runnerId\":1,\"lockMode\":\""+mode+"\"}");
    }

    /** 保留读共享写互斥规则，网络通知在本组数据库测试中为空操作。 */
    private class Handler extends GlobalLockHandlerBase {
        public String getHandler() { return "deploy"; }
        public String getHandlerName() { return "test"; }
        /** 测试业务处理器按自身元数据确认归属。 */
        public boolean ownsLock(GlobalLockVo lock,String ownerId) { return ownerId.equals(lock.getHandlerParam().getString("jobId")); }
        public JSONObject getLock(JSONObject param) { return null; }
        public JSONObject retryLock(Long id,JSONObject param) { return null; }
        public void validateAcquisition(GlobalLockVo lock) {
            Long jobId=lock.getHandlerParam().getLong("jobId"); lock.setOwnerId(jobId.toString());
            String status=(String)jdbc.queryForMap(sharedSql,jobId).get("status");
            if (!"running".equals(status)) throw new DataIntegrityViolationException("terminal");
        }
        public boolean getIsCanLock(List<GlobalLockVo> locks,GlobalLockVo request) {
            return locks.stream().filter(lock -> lock.getIsLock()==1).allMatch(lock -> "read".equals(lock.getHandlerParam().getString("lockMode")) && "read".equals(request.getHandlerParam().getString("lockMode")));
        }
    }
}
