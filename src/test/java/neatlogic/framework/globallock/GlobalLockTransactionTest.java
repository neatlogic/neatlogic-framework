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

import neatlogic.framework.transaction.util.TransactionUtil;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.dao.DataAccessResourceFailureException;
import static org.junit.Assert.*;

/** 验证完整事务重试边界；数据库错误码为故障注入，不作为 InnoDB 并发证明。 */
public class GlobalLockTransactionTest {
    private static final Logger logger = LoggerFactory.getLogger(GlobalLockTransactionTest.class);
    private JdbcDataSource source;
    private JdbcTemplate jdbc;

    /** 每次使用独立数据库，不连接业务环境。 */
    @Before public void setup() {
        source = new JdbcDataSource();
        source.setURL("jdbc:h2:mem:" + UUID.randomUUID() + ";DB_CLOSE_DELAY=-1");
        jdbc = new JdbcTemplate(source);
        jdbc.execute("CREATE TABLE attempts(id INT PRIMARY KEY)");
        new TransactionUtil(new DataSourceTransactionManager(source));
    }

    /** 首次尝试的写入回滚，后续尝试获得新的连接资源且只提交一次。 */
    @Test public void retriesWholeTransactionOnNewResource() {
        AtomicInteger calls = new AtomicInteger();
        Set<Object> resources = Collections.newSetFromMap(new IdentityHashMap<>());
        GlobalLockTransaction.execute(() -> {
            resources.add(TransactionSynchronizationManager.getResource(source));
            jdbc.update("INSERT INTO attempts VALUES(1)");
            if (calls.getAndIncrement() < 2) throw conflict(1213);
        });
        assertEquals(3, calls.get()); assertEquals(3, resources.size());
        assertEquals(Integer.valueOf(1), jdbc.queryForObject("SELECT COUNT(*) FROM attempts", Integer.class));
    }

    /** 参与外层事务时不能局部重试或提交外层的其他写入。 */
    @Test public void outerTransactionIsRollbackOnlyWithoutRetry() {
        TransactionStatus outer = TransactionUtil.openTx();
        AtomicInteger calls = new AtomicInteger();
        jdbc.update("INSERT INTO attempts VALUES(2)");
        try {
            GlobalLockTransaction.execute(() -> { calls.incrementAndGet(); throw conflict(1205); });
            fail("expected conflict");
        } catch (RuntimeException expected) {
            logger.error("Expected injected conflict", expected);
            assertTrue(outer.isRollbackOnly()); assertEquals(1, calls.get());
        } finally { TransactionUtil.rollbackTx(outer); }
        assertEquals(Integer.valueOf(0), jdbc.queryForObject("SELECT COUNT(*) FROM attempts", Integer.class));
    }

    /** 连接断开不属于可重放冲突，最多执行一次。 */
    @Test public void unknownOutcomeDoesNotRetry() {
        AtomicInteger calls = new AtomicInteger();
        try {
            GlobalLockTransaction.execute(() -> { calls.incrementAndGet(); throw conflict(2013); });
            fail("expected connection failure");
        } catch (RuntimeException expected) { logger.error("Expected connection failure", expected); }
        assertEquals(1, calls.get());
    }

    /** 中断立即结束，不清除调用线程的中断标记。 */
    @Test public void interruptionStopsRetries() {
        AtomicInteger calls = new AtomicInteger();
        try {
            GlobalLockTransaction.execute(() -> { calls.incrementAndGet(); Thread.currentThread().interrupt(); throw conflict(1213); });
            fail("expected interruption");
        } catch (RuntimeException expected) {
            logger.error("Expected interrupted conflict", expected);
            assertTrue(Thread.currentThread().isInterrupted());
        } finally { Thread.interrupted(); }
        assertEquals(1, calls.get());
    }

    /** 构造带 MySQL 错误码的底层异常以验证分类。 */
    private RuntimeException conflict(int code) { return new DataAccessResourceFailureException("injected", new SQLException("injected", "HY000", code)); }
}
