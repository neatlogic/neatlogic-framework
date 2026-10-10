package neatlogic.framework.healthcheck;

import neatlogic.framework.dao.plugin.SqlCostInterceptor;
import neatlogic.framework.dto.healthcheck.RequestSqlAuditVo;
import neatlogic.framework.dto.healthcheck.SqlAuditVo;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.Assert.*;

/**
 * 验证两类审计记录的容量、模式切换和并发快照语义。
 */
public class SqlAuditManagerTest {
    /**
     * 每个用例恢复当前进程的默认监控状态，避免静态缓存互相影响。
     */
    @Before
    @After
    public void reset() {
        SqlAuditManager.clearAllSqlAudit();
        SqlAuditManager.updateSaveMode(SqlAuditManager.SAVE_MODE_RECENT);
        SqlCostInterceptor.SqlIdMap.clear();
        SqlCostInterceptor.UrlMap.clear();
    }

    /**
     * 最近模式的两个缓存均只保留最后写入的1000条记录。
     */
    @Test
    public void recentModeEvictsOldestRecordsInBothCaches() {
        for (int i = 0; i < 1005; i++) {
            SqlAuditManager.addSqlAudit(sql("query" + i, 2000 - i));
            SqlAuditManager.addRequestSqlAudit(request(i, "/request/" + i, 2000 - i));
        }
        SqlAuditManager.Snapshot snapshot = SqlAuditManager.getSnapshot();
        assertEquals("recent", snapshot.getSaveMode());
        assertEquals(1000, snapshot.getSqlAuditList().size());
        assertEquals("query5", snapshot.getSqlAuditList().get(0).getId());
        assertEquals("query1004", snapshot.getSqlAuditList().get(999).getId());
        assertEquals(1000, snapshot.getRequestSqlAuditList().size());
        assertEquals(Long.valueOf(5), snapshot.getRequestSqlAuditList().get(0).getId());
        assertEquals(Long.valueOf(1004), snapshot.getRequestSqlAuditList().get(999).getId());
    }

    /**
     * 早期慢SQL不会被后续快SQL淘汰，新慢SQL能够替换最短记录。
     */
    @Test
    public void slowestModeKeepsLongestSqlExecutionsWithoutDeduplication() {
        SqlAuditManager.updateSaveMode("slowest");
        SqlAuditManager.addSqlAudit(sql("earlySlow", 5000));
        for (int i = 0; i < 999; i++) {
            SqlAuditManager.addSqlAudit(sql("repeatQuery", 100));
        }
        for (int i = 0; i < 100; i++) {
            SqlAuditManager.addSqlAudit(sql("fast", 1));
        }
        SqlAuditManager.addSqlAudit(sql("newSlow", 200));
        List<SqlAuditVo> list = SqlAuditManager.getSqlAuditList();
        assertEquals(1000, list.size());
        assertEquals("earlySlow", list.get(0).getId());
        assertEquals("newSlow", list.get(1).getId());
        assertEquals(998, list.stream().filter(vo -> "repeatQuery".equals(vo.getId())).count());
        assertFalse(list.stream().anyMatch(vo -> "fast".equals(vo.getId())));
    }

    /**
     * 耗时相同时保留较新执行，并按新到旧展示；请求表遵循相同规则。
     */
    @Test
    public void equalCostsKeepAndDisplayNewerRecordsFirst() {
        SqlAuditManager.updateSaveMode("slowest");
        for (int i = 0; i < 1001; i++) {
            SqlAuditManager.addSqlAudit(sql("query" + i, 10));
            SqlAuditManager.addRequestSqlAudit(request(i, "/request", 10));
        }
        SqlAuditManager.Snapshot snapshot = SqlAuditManager.getSnapshot();
        assertEquals("query1000", snapshot.getSqlAuditList().get(0).getId());
        assertEquals("query1", snapshot.getSqlAuditList().get(999).getId());
        assertEquals(Long.valueOf(1000), snapshot.getRequestSqlAuditList().get(0).getId());
        assertEquals(Long.valueOf(1), snapshot.getRequestSqlAuditList().get(999).getId());
    }

    /**
     * URL比较使用全部SQL累计耗时，同时保留缓存命中和同ID明细统计。
     */
    @Test
    public void requestRetentionUsesCompleteAggregatedCostAndPreservesDetails() {
        SqlAuditManager.updateSaveMode("slowest");
        RequestSqlAuditVo aggregate = request(0, "/aggregate", 60);
        SqlAuditVo cached = sql("query", 60);
        cached.setUseCacheLevel("一级缓存");
        aggregate.addSqlAudit(cached);
        SqlAuditManager.addRequestSqlAudit(aggregate);
        for (int i = 1; i < 1001; i++) {
            SqlAuditManager.addRequestSqlAudit(request(i, "/single", 100));
        }
        List<RequestSqlAuditVo> list = SqlAuditManager.getRequestSqlAuditList();
        assertEquals(1000, list.size());
        RequestSqlAuditVo saved = list.get(0);
        assertEquals(Long.valueOf(0), saved.getId());
        assertEquals(120, saved.getTotalTimeCost());
        assertEquals(60, saved.getNotUseCacheTotalTimeCost());
        assertEquals(2, saved.getSqlCount());
        assertEquals("tenant", saved.getTenant());
        assertEquals("user", saved.getUserId());
        assertEquals("worker", saved.getThreadName());
        assertEquals(new Date(10), saved.getRunTime());
        RequestSqlAuditVo.SameIdSqlAuditVo sameId = saved.getSameIdSqlAuditList().get(0);
        assertEquals(2, sameId.getSqlAuditList().size());
        assertEquals(120, sameId.getTotalTimeCost());
        assertEquals(1, sameId.getNotUseCacheCount());
        assertEquals(2, sameId.getTimeCostList().size());
        assertEquals("一级缓存", sameId.getUseCacheLevelList().get(1));
        // 请求已经完成复制，后续改变原始聚合对象不会改变已保留统计。
        aggregate.addSqlAudit(sql("later", 1000));
        assertEquals(2, saved.getSqlCount());
        SqlAuditManager.addRequestSqlAudit(aggregate);
        assertEquals(1000, SqlAuditManager.getRequestSqlAuditList().size());
        assertEquals(120, SqlAuditManager.getRequestSqlAuditList().get(0).getTotalTimeCost());
    }

    /**
     * 切换不清空、不重排内部缓存；切回最近模式后仍从最早写入记录开始淘汰。
     */
    @Test
    public void switchingModesRetainsInsertionOrderAndExistingRecords() {
        SqlAuditManager.addSqlAudit(sql("earlySlow", 5000));
        for (int i = 1; i < 1000; i++) {
            SqlAuditManager.addSqlAudit(sql("query" + i, i));
        }
        assertEquals("slowest", SqlAuditManager.updateSaveMode("slowest"));
        assertEquals(1000, SqlAuditManager.getSnapshot().getSqlAuditList().size());
        SqlAuditManager.addSqlAudit(sql("newSlow", 6000));
        assertEquals("newSlow", SqlAuditManager.getSqlAuditList().get(0).getId());
        assertEquals("recent", SqlAuditManager.updateSaveMode("recent"));
        assertEquals("earlySlow", SqlAuditManager.getSqlAuditList().get(0).getId());
        SqlAuditManager.addSqlAudit(sql("latest", 0));
        List<SqlAuditVo> recent = SqlAuditManager.getSqlAuditList();
        assertEquals("query2", recent.get(0).getId());
        assertEquals("latest", recent.get(999).getId());
    }

    /**
     * 查询列表允许独立排序或清空，并且已有快照不会随模式切换发生变化。
     */
    @Test
    public void snapshotsAndLegacyGettersDoNotExposeInternalLists() {
        SqlAuditManager.addSqlAudit(sql("query", 10));
        SqlAuditManager.addRequestSqlAudit(request(1, "/query", 10));
        SqlAuditManager.Snapshot snapshot = SqlAuditManager.getSnapshot();
        SqlAuditManager.getSqlAuditList().clear();
        SqlAuditManager.getRequestSqlAuditList().clear();
        SqlAuditManager.updateSaveMode("slowest");
        assertEquals("recent", snapshot.getSaveMode());
        snapshot.getSqlAuditList().clear();
        snapshot.getRequestSqlAuditList().clear();
        assertEquals(1, SqlAuditManager.getSqlAuditList().size());
        assertEquals(1, SqlAuditManager.getRequestSqlAuditList().size());
    }

    /**
     * 删除监控项及清空沿用原有范围，清空不会重置保存模式。
     */
    @Test
    public void removalAndClearPreserveModeAndOtherMonitoringTargets() {
        SqlAuditManager.updateSaveMode("slowest");
        SqlCostInterceptor.SqlIdMap.addId("keep");
        SqlCostInterceptor.SqlIdMap.addId("remove");
        SqlCostInterceptor.UrlMap.addUrl("/keep");
        SqlCostInterceptor.UrlMap.addUrl("/remove");
        SqlAuditManager.addSqlAudit(sql("Mapper.keep", 10));
        SqlAuditManager.addSqlAudit(sql("Mapper.remove", 20));
        SqlAuditManager.addRequestSqlAudit(request(1, "/keep", 10));
        SqlAuditManager.addRequestSqlAudit(request(2, "/remove", 20));
        SqlCostInterceptor.SqlIdMap.removeId("remove");
        SqlCostInterceptor.UrlMap.removeUrl("/remove");
        SqlAuditManager.removeSqlAudit("remove");
        SqlAuditManager.removeRequestSqlAudit("/remove");
        assertEquals("Mapper.keep", SqlAuditManager.getSqlAuditList().get(0).getId());
        assertEquals("/keep", SqlAuditManager.getRequestSqlAuditList().get(0).getUrl());
        SqlAuditManager.clearSqlAudit();
        assertTrue(SqlAuditManager.getSqlAuditList().isEmpty());
        assertEquals(1, SqlAuditManager.getRequestSqlAuditList().size());
        SqlAuditManager.clearRequestSqlAudit();
        assertTrue(SqlAuditManager.getRequestSqlAuditList().isEmpty());
        assertEquals("slowest", SqlAuditManager.getSaveMode());
        SqlAuditManager.addSqlAudit(sql("another", 10));
        SqlAuditManager.addRequestSqlAudit(request(3, "/another", 10));
        SqlAuditManager.clearAllSqlAudit();
        assertTrue(SqlAuditManager.getSnapshot().getSqlAuditList().isEmpty());
        assertTrue(SqlAuditManager.getSnapshot().getRequestSqlAuditList().isEmpty());
        assertEquals("slowest", SqlAuditManager.getSaveMode());
    }

    /**
     * 非法模式不会改变现有模式或已采集数据。
     */
    @Test
    public void invalidModeDoesNotAlterCurrentState() {
        SqlAuditManager.addSqlAudit(sql("query", 10));
        try {
            SqlAuditManager.updateSaveMode("invalid");
            fail("应拒绝非法模式");
        } catch (IllegalArgumentException expected) {
            assertEquals("recent", SqlAuditManager.getSaveMode());
            assertEquals(1, SqlAuditManager.getSqlAuditList().size());
        }
    }

    /**
     * 并发采集、切换、清空和查询时，两表容量不超限，快照排序与所附模式一致。
     */
    @Test(timeout = 30000)
    public void concurrentWritesSwitchesAndSnapshotsStayConsistent() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(5);
        CountDownLatch start = new CountDownLatch(1);
        List<Future<?>> futures = new ArrayList<>();
        try {
            futures.add(executor.submit(() -> {
                await(start);
                for (int i = 0; i < 2000; i++) {
                    SqlAuditManager.addSqlAudit(sql("query" + i, i % 57));
                }
            }));
            futures.add(executor.submit(() -> {
                await(start);
                for (int i = 0; i < 1500; i++) {
                    SqlAuditManager.addRequestSqlAudit(request(i, "/query", i % 53));
                }
            }));
            futures.add(executor.submit(() -> {
                await(start);
                for (int i = 0; i < 200; i++) {
                    SqlAuditManager.updateSaveMode("slowest");
                    SqlAuditManager.updateSaveMode("recent");
                }
            }));
            futures.add(executor.submit(() -> {
                await(start);
                for (int i = 0; i < 10; i++) {
                    SqlAuditManager.clearAllSqlAudit();
                }
            }));
            futures.add(executor.submit(() -> {
                await(start);
                for (int i = 0; i < 300; i++) {
                    SqlAuditManager.Snapshot snapshot = SqlAuditManager.getSnapshot();
                    assertTrue(snapshot.getSqlAuditList().size() <= 1000);
                    assertTrue(snapshot.getRequestSqlAuditList().size() <= 1000);
                    if ("slowest".equals(snapshot.getSaveMode())) {
                        long previousCost = Long.MAX_VALUE;
                        for (SqlAuditVo vo : snapshot.getSqlAuditList()) {
                            assertTrue(previousCost >= vo.getTimeCost());
                            previousCost = vo.getTimeCost();
                        }
                        previousCost = Long.MAX_VALUE;
                        for (RequestSqlAuditVo vo : snapshot.getRequestSqlAuditList()) {
                            assertTrue(previousCost >= vo.getTotalTimeCost());
                            previousCost = vo.getTotalTimeCost();
                        }
                    }
                }
            }));
            start.countDown();
            for (Future<?> future : futures) {
                future.get(25, TimeUnit.SECONDS);
            }
        } finally {
            executor.shutdownNow();
        }
    }

    /**
     * 构造带来源信息的SQL记录供缓存和请求聚合用例使用。
     */
    private static SqlAuditVo sql(String id, long timeCost) {
        SqlAuditVo vo = new SqlAuditVo();
        vo.setId(id);
        vo.setTimeCost(timeCost);
        vo.setRunTime(new Date(10));
        vo.setSql("select 1");
        vo.setTenant("tenant");
        vo.setUserId("user");
        vo.setThreadName("worker");
        return vo;
    }

    /**
     * 构造已经采集明细、尚待请求结束聚合的对象。
     */
    private static RequestSqlAuditVo request(long id, String url, long timeCost) {
        RequestSqlAuditVo vo = new RequestSqlAuditVo(id, url, "worker");
        vo.addSqlAudit(sql("query", timeCost));
        return vo;
    }

    /**
     * 同时启动并发任务，并正确传递线程中断。
     */
    private static void await(CountDownLatch start) {
        try {
            start.await();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("并发测试被中断", ex);
        }
    }
}
