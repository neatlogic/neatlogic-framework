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
import neatlogic.framework.dto.globallock.GlobalLockVo;
import neatlogic.framework.globallock.core.*;
import neatlogic.framework.globallock.dao.mapper.GlobalLockMapper;
import neatlogic.framework.transaction.util.TransactionUtil;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionStatus;
import java.lang.reflect.*;
import java.util.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.atomic.AtomicBoolean;
import neatlogic.framework.globallock.exception.GlobalLockReleaseException;
import org.slf4j.LoggerFactory;
import static org.junit.Assert.*;

/** 使用隔离的 H2 数据库验证真实事务边界，不调用客户服务或脚本。 */
public class GlobalLockManagerTest {
    private JdbcDataSource source;
    private JdbcTemplate jdbc;
    private final List<Long> notified = new ArrayList<>();
    private final Set<Long> failures = new HashSet<>();
    private Map<String, IGlobalLockHandler> handlers;
    private IGlobalLockHandler previous;
    private boolean deleteFails;
    private GlobalLockI18nFixture i18n;

    /** 初始化隔离数据库和真实异常语言包，验证释放失败的持久化内容。 */
    @Before public void setup() throws Exception {
        i18n = new GlobalLockI18nFixture();
        source = new JdbcDataSource(); source.setURL("jdbc:h2:mem:" + UUID.randomUUID() + ";DB_CLOSE_DELAY=-1");
        jdbc = new JdbcTemplate(source);
        jdbc.execute("CREATE TABLE locks(id BIGINT PRIMARY KEY, held INT, job BIGINT, error VARCHAR, revision BIGINT DEFAULT 0)");
        new TransactionUtil(new DataSourceTransactionManager(source));
        GlobalLockMapper mapper = (GlobalLockMapper) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{GlobalLockMapper.class}, (proxy, method, args) -> {
            switch (method.getName()) {
                case "getLockCount": return 0;
                case "getGlobalLockById": return rows("SELECT * FROM locks WHERE id=" + args[0]).stream().findFirst().orElse(null);
                case "getWaitingLocks": return rows("SELECT * FROM locks WHERE held=0 ORDER BY id");
                case "getOwnerLockCandidates":
                case "getLockCandidates": return rows("SELECT * FROM locks ORDER BY id");
                case "getGlobalLockPkByUuidForUpdate": return "resource";
                case "deleteLock":
                    if (deleteFails) throw new IllegalStateException("delete failed");
                    jdbc.update("DELETE FROM locks WHERE id=?", args[0]); return null;
                case "incrementNotifyRevision": return jdbc.update("UPDATE locks SET revision=revision+1 WHERE id=? AND held=0",args[0]);
                case "getNotifyRevision": return jdbc.queryForObject("SELECT revision FROM locks WHERE id=?",Long.class,args[0]);
                case "updateLockError": jdbc.update("UPDATE locks SET error=?,revision=? WHERE id=? AND revision<=?", args[2], args[3], args[0], args[3]); return null;
                default: return null;
            }
        });
        new GlobalLockManager(mapper).setOperations(new GlobalLockOperationManager());
        Field field = GlobalLockHandlerFactory.class.getDeclaredField("handlerMap"); field.setAccessible(true);
        handlers = (Map<String, IGlobalLockHandler>) field.get(null); previous = handlers.get("deploy");
        handlers.put("deploy", new Handler());
    }
    /** 恢复处理器和语言上下文，避免影响其他用例。 */
    @After public void restore() throws Exception {
        if (previous == null) handlers.remove("deploy"); else handlers.put("deploy", previous);
        if (i18n != null) i18n.close();
    }
    private List<GlobalLockVo> rows(String sql) {
        return jdbc.query(sql, (rs, n) -> {
            GlobalLockVo lock = new GlobalLockVo(rs.getLong("id"), "deploy", "1/2/workspace", "{\"jobId\":" + rs.getLong("job") + "}");
            lock.setIsLock(rs.getInt("held")); lock.setNotifyError(rs.getString("error")); return lock;
        });
    }
    private void add(long id, int held, long job) { jdbc.update("INSERT INTO locks(id,held,job) VALUES(?,?,?)", id, held, job); }
    private class Handler extends GlobalLockHandlerBase {
        public String getHandler() { return "deploy"; }
        public String getHandlerName() { return "test"; }
        /** 测试处理器自行解释业务归属，框架不读取作业参数。 */
        public boolean ownsLock(GlobalLockVo lock, String ownerId) { return ownerId.equals(lock.getHandlerParam().getString("jobId")); }
        public boolean getIsCanLock(List<GlobalLockVo> list, GlobalLockVo lock) { return true; }
        public JSONObject getLock(JSONObject p) { return null; }
        public JSONObject retryLock(Long id, JSONObject p) { return null; }
        @Override protected void myDoNotify(GlobalLockVo lock, JSONObject p) {
            // 使用独立物理连接确认删除已提交，而非仅在当前事务内可见。
            try (Connection connection = source.getConnection(); Statement statement = connection.createStatement()) {
                ResultSet rs = statement.executeQuery("SELECT COUNT(*) FROM locks WHERE id=1"); rs.next(); assertEquals(0, rs.getInt(1));
            } catch (SQLException ex) { throw new AssertionError(ex); }
            notified.add(lock.getId());
            if (failures.contains(lock.getId())) throw new IllegalStateException("errno=111");
        }
    }
    @Test public void committedDeleteSurvivesBadWaiterAndNotifiesNext() {
        add(1,1,10); add(2,0,20); add(3,0,30); add(4,0,40); failures.add(2L);
        TransactionStatus outer = TransactionUtil.openTx();
        GlobalLockManager.unLock(1L,new JSONObject()); assertTrue(notified.isEmpty());
        TransactionUtil.commitTx(outer);
        assertEquals(Arrays.asList(2L,3L),notified);
        assertEquals(1,(int)jdbc.queryForObject("SELECT COUNT(*) FROM locks WHERE id=2",Integer.class));
        assertTrue(jdbc.queryForObject("SELECT error FROM locks WHERE id=2",String.class).contains("errno=111"));
    }
    @Test public void outerRollbackRestoresLockAndDoesNotNotify() {
        add(1,1,10); add(2,0,20);
        TransactionStatus outer = TransactionUtil.openTx(); GlobalLockManager.unLock(1L,new JSONObject()); TransactionUtil.rollbackTx(outer);
        assertTrue(notified.isEmpty()); assertEquals(2,(int)jdbc.queryForObject("SELECT COUNT(*) FROM locks",Integer.class));
    }
    @Test public void allFailedWaitersAreRetainedWithoutFailingRelease() {
        add(1,1,10); add(2,0,20); add(3,0,30); failures.addAll(Arrays.asList(2L,3L));
        GlobalLockManager.unLock(1L,new JSONObject());
        assertEquals(Arrays.asList(2L,3L),notified); assertEquals(2,(int)jdbc.queryForObject("SELECT COUNT(*) FROM locks",Integer.class));
        GlobalLockManager.unLock(1L,new JSONObject()); assertEquals(2,notified.size());
    }
    @Test public void terminalCleanupDeletesOnlyOwnHeldAndWaitingRecords() {
        add(1,1,10); add(2,0,10); add(3,0,30);
        TransactionStatus tx = TransactionUtil.openTx(); GlobalLockManager.releaseOwnedLocks(Collections.singletonList("deploy"), "10"); TransactionUtil.commitTx(tx);
        assertEquals(Collections.singletonList(3L),notified); assertEquals(1,(int)jdbc.queryForObject("SELECT COUNT(*) FROM locks",Integer.class));
    }
    /** 异常返回包含锁上下文，且仅在外层回滚后持久化相同诊断信息。 */
    @Test public void deleteErrorIsSavedOnlyAfterOuterRollback() {
        add(1,1,10); deleteFails=true;
        TransactionStatus tx = TransactionUtil.openTx();
        try { GlobalLockManager.unLock(1L,new JSONObject()); fail("expected deletion failure"); }
        catch (GlobalLockReleaseException expected) {
            LoggerFactory.getLogger(getClass()).error("Expected release rejection", expected);
            assertTrue(expected.getMessage().contains("lockId=1"));
            assertTrue(expected.getMessage().contains("1/2/workspace"));
            assertEquals("delete failed", expected.getCause().getMessage());
        }
        assertNull(jdbc.queryForObject("SELECT error FROM locks WHERE id=1",String.class));
        TransactionUtil.rollbackTx(tx);
        assertTrue(jdbc.queryForObject("SELECT error FROM locks WHERE id=1",String.class).contains("delete failed"));
    }
    /** 未附加业务策略的处理器可使用任意归属，不能要求作业参数。 */
    @Test public void defaultHandlerAcceptsNonJobOwner() {
        GlobalLockVo lock = new GlobalLockVo("generic", "arbitrary:key", "{}");
        lock.setOwnerId("tenant-config:abc");
        IGlobalLockHandler generic = new GlobalLockHandlerBase() {
            public String getHandler() { return "generic"; }
            public String getHandlerName() { return "generic"; }
            public boolean getIsCanLock(List<GlobalLockVo> locks, GlobalLockVo request) { return true; }
            public JSONObject getLock(JSONObject param) { return null; }
            public JSONObject retryLock(Long id, JSONObject param) { return null; }
        };
        generic.validateAcquisition(lock);
        generic.validateIdentity(lock, lock);
        assertTrue(generic.ownsLock(lock, "tenant-config:abc"));
        assertFalse(generic.ownsLock(lock, "different"));
    }
    /** 通用搜索必须保留处理器的原有筛选入口，不能套用发布作业字段。 */
    @Test public void searchDelegatesToExistingHandlerHook() {
        AtomicBoolean called = new AtomicBoolean();
        handlers.put("deploy", new Handler() {
            @Override public void initSearchParam(GlobalLockVo query) {
                called.set(true);
                assertEquals("opaque", query.getKeywordParam().getString("customFilter"));
                query.setIdList(Collections.singletonList(99L));
            }
        });
        GlobalLockVo query = new GlobalLockVo(); query.setHandler("deploy");
        query.setKeywordParam(JSONObject.parseObject("{\"customFilter\":\"opaque\"}"));
        GlobalLockManager.searchGlobalLock(query);
        assertTrue(called.get()); assertEquals(Collections.singletonList(99L), query.getIdList());
    }
}
