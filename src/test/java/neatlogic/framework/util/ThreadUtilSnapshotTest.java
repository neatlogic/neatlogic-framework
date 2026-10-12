package neatlogic.framework.util;

import neatlogic.framework.dto.healthcheck.ThreadSnapshotFrameVo;
import neatlogic.framework.dto.healthcheck.ThreadSnapshotTaskVo;
import neatlogic.framework.dto.healthcheck.ThreadSnapshotThreadVo;
import neatlogic.framework.dto.healthcheck.ThreadSnapshotVo;
import org.junit.Test;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import static org.junit.Assert.*;

/** 验证线程快照的同源证据、监控降级与真实锁关系。 */
public class ThreadUtilSnapshotTest {
    /** 同一次 JVM 转储生成结构和原文，不重新抓取调用栈。 */
    @Test
    public void capturesOneDumpAndMarksReportedDeadlocks() {
        BeanFixture fixture = new BeanFixture();
        long id = Thread.currentThread().getId();
        fixture.deadlockIds = new long[]{id};
        ThreadSnapshotVo snapshot = ThreadUtil.captureThreadSnapshot(fixture.bean());
        assertEquals(1, fixture.count("dumpAllThreads"));
        assertTrue(snapshot.isDeadlockDetectionAvailable());
        assertEquals(String.valueOf(id), snapshot.getDeadlockedThreadIds().get(0));
        ThreadSnapshotThreadVo thread = snapshot.getThreads().get(0);
        assertTrue(thread.isDeadlocked());
        assertEquals(Thread.currentThread().getName(), thread.getName());
        assertEquals(1.5, thread.getCpuTimeMs(), 0.00001);
        assertEquals(0.5, thread.getUserTimeMs(), 0.00001);
        assertTrue(snapshot.getRawText().contains("tid=" + thread.getId()));
        for (ThreadSnapshotFrameVo frame : thread.getStackTrace()) {
            assertTrue(snapshot.getRawText().contains(frame.getText()));
        }
        assertTrue(snapshot.getCaptureDurationMs() >= 0);
        assertNotNull(snapshot.getProcessInstanceId());
    }

    /** 未启用 CPU 统计时不主动打开 JVM 监控。 */
    @Test
    public void leavesDisabledCpuMonitoringUnchanged() {
        BeanFixture fixture = new BeanFixture();
        fixture.cpuEnabled = false;
        ThreadSnapshotVo snapshot = ThreadUtil.captureThreadSnapshot(fixture.bean());
        assertTrue(snapshot.isCpuTimeSupported());
        assertFalse(snapshot.isCpuTimeEnabled());
        assertNull(snapshot.getThreads().get(0).getCpuTimeMs());
        assertTrue(snapshot.getWarningCodes().contains("CPU_TIME_DISABLED"));
        assertEquals(0, fixture.count("setThreadCpuTimeEnabled"));
        assertEquals(0, fixture.count("setThreadContentionMonitoringEnabled"));
        assertEquals(0, fixture.count("getThreadCpuTime"));
    }

    /** 不支持 CPU 和锁信息的 JVM 仍可返回完整线程栈。 */
    @Test
    public void supportsLimitedJvmAndNullExitedThreads() {
        BeanFixture fixture = new BeanFixture();
        fixture.cpuSupported = false;
        fixture.monitorSupported = false;
        fixture.synchronizerSupported = false;
        fixture.includeNullThread = true;
        ThreadSnapshotVo snapshot = ThreadUtil.captureThreadSnapshot(fixture.bean());
        assertEquals(1, snapshot.getThreads().size());
        assertNull(snapshot.getThreads().get(0).getCpuTimeMs());
        assertTrue(snapshot.getWarningCodes().contains("CPU_TIME_UNSUPPORTED"));
        assertTrue(snapshot.getWarningCodes().contains("MONITOR_INFORMATION_UNSUPPORTED"));
        assertTrue(snapshot.getWarningCodes().contains("SYNCHRONIZER_INFORMATION_UNSUPPORTED"));
        assertEquals(1, fixture.count("findMonitorDeadlockedThreads"));
        assertEquals(0, fixture.count("findDeadlockedThreads"));
        assertEquals(0, fixture.count("isThreadCpuTimeEnabled"));
    }

    /** 已退出线程的负 CPU 计数为空，不转成负时间。 */
    @Test
    public void negativeCpuCountersAreUnavailable() {
        BeanFixture fixture = new BeanFixture();
        fixture.cpuNanos = -1L;
        fixture.userNanos = -1L;
        ThreadSnapshotVo snapshot = ThreadUtil.captureThreadSnapshot(fixture.bean());
        assertNull(snapshot.getThreads().get(0).getCpuTimeMs());
        assertNull(snapshot.getThreads().get(0).getUserTimeMs());
    }

    /** 锁转储和死锁检测受限时保持可用数据并注明缺失能力。 */
    @Test
    public void recoversFromUnsupportedLockDumpAndDeadlockDetection() {
        BeanFixture fixture = new BeanFixture();
        fixture.dumpDenied = true;
        fixture.deadlockDenied = true;
        fixture.cpuDenied = true;
        ThreadSnapshotVo snapshot = ThreadUtil.captureThreadSnapshot(fixture.bean());
        assertEquals(1, snapshot.getThreads().size());
        assertFalse(snapshot.isMonitorSupported());
        assertFalse(snapshot.isSynchronizerSupported());
        assertFalse(snapshot.isDeadlockDetectionAvailable());
        assertTrue(snapshot.getWarningCodes().contains("LOCK_INFORMATION_UNAVAILABLE"));
        assertTrue(snapshot.getWarningCodes().contains("DEADLOCK_DETECTION_UNAVAILABLE"));
        assertTrue(snapshot.getWarningCodes().contains("CPU_TIME_UNAVAILABLE"));
        assertEquals(1, fixture.count("getThreadInfo"));
        assertNull(snapshot.getThreads().get(0).getCpuTimeMs());
    }

    /** 快照标识每次更新，进程标识在同一 JVM 中保持稳定。 */
    @Test
    public void keepsProcessIdentityAndFrozenTaskDuration() {
        BeanFixture fixture = new BeanFixture();
        ThreadSnapshotVo first = ThreadUtil.captureThreadSnapshot(fixture.bean());
        ThreadSnapshotVo second = ThreadUtil.captureThreadSnapshot(fixture.bean());
        assertNotEquals(first.getSnapshotId(), second.getSnapshotId());
        assertEquals(first.getProcessInstanceId(), second.getProcessInstanceId());
        assertTrue(second.getMonotonicTimeMs() >= first.getMonotonicTimeMs());
        ThreadSnapshotTaskVo task = new ThreadSnapshotTaskVo();
        task.setStartTime(new Date(0L));
        task.setTimeCost(123L);
        assertEquals(123L, task.getTimeCost());
    }

    /** 用可释放的对象锁验证持有者与等待者，避免构造无法回收的死锁线程。 */
    @Test
    public void capturesActualMonitorOwnerAndBlockedWaiter() throws Exception {
        Object lock = new Object();
        CountDownLatch acquired = new CountDownLatch(1);
        CountDownLatch release = new CountDownLatch(1);
        Thread owner = new Thread(() -> {
            synchronized (lock) {
                acquired.countDown();
                try {
                    release.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }, "snapshot-test-lock-owner");
        Thread waiter = new Thread(() -> {
            synchronized (lock) {
                // 获得锁后立即退出，使测试资源能够回收。
            }
        }, "snapshot-test-lock-waiter");
        owner.setDaemon(true);
        waiter.setDaemon(true);
        owner.start();
        try {
            assertTrue(acquired.await(3, TimeUnit.SECONDS));
            waiter.start();
            long timeout = System.nanoTime() + TimeUnit.SECONDS.toNanos(3);
            while (waiter.getState() != Thread.State.BLOCKED && System.nanoTime() < timeout) {
                Thread.yield();
            }
            assertEquals(Thread.State.BLOCKED, waiter.getState());
            ThreadSnapshotVo snapshot = ThreadUtil.captureThreadSnapshot();
            ThreadSnapshotThreadVo ownerVo = findThread(snapshot, owner.getId());
            ThreadSnapshotThreadVo waiterVo = findThread(snapshot, waiter.getId());
            assertEquals("BLOCKED", waiterVo.getState());
            assertEquals(String.valueOf(owner.getId()), waiterVo.getLockOwnerId());
            assertTrue(ownerVo.getOwnedMonitorNames().contains(waiterVo.getLockName()));
            assertTrue(snapshot.getRawText().contains("locked monitor " + waiterVo.getLockName()));
        } finally {
            release.countDown();
            owner.join(3000);
            waiter.join(3000);
        }
    }

    /** 可中断同步器死锁能被 JVM 识别，结束时中断线程释放锁而不遗留死锁。 */
    @Test
    public void detectsActualInterruptibleSynchronizerDeadlock() throws Exception {
        ReentrantLock first = new ReentrantLock();
        ReentrantLock second = new ReentrantLock();
        CountDownLatch owned = new CountDownLatch(2);
        Thread one = new Thread(() -> holdThenAwait(first, second, owned), "snapshot-test-deadlock-one");
        Thread two = new Thread(() -> holdThenAwait(second, first, owned), "snapshot-test-deadlock-two");
        one.setDaemon(true);
        two.setDaemon(true);
        one.start();
        two.start();
        try {
            assertTrue(owned.await(3, TimeUnit.SECONDS));
            ThreadMXBean bean = ManagementFactory.getThreadMXBean();
            long timeout = System.nanoTime() + TimeUnit.SECONDS.toNanos(3);
            long[] ids = bean.findDeadlockedThreads();
            while (ids == null && System.nanoTime() < timeout) {
                Thread.yield();
                ids = bean.findDeadlockedThreads();
            }
            assertNotNull("测试应构成可检测的同步器死锁", ids);
            ThreadSnapshotVo snapshot = ThreadUtil.captureThreadSnapshot();
            assertTrue(snapshot.getDeadlockedThreadIds().contains(String.valueOf(one.getId())));
            assertTrue(snapshot.getDeadlockedThreadIds().contains(String.valueOf(two.getId())));
            ThreadSnapshotThreadVo oneVo = findThread(snapshot, one.getId());
            ThreadSnapshotThreadVo twoVo = findThread(snapshot, two.getId());
            assertTrue(oneVo.isDeadlocked());
            assertTrue(twoVo.isDeadlocked());
            assertEquals(twoVo.getId(), oneVo.getLockOwnerId());
            assertEquals(oneVo.getId(), twoVo.getLockOwnerId());
            assertTrue(oneVo.getOwnedSynchronizerNames().contains(twoVo.getLockName()));
        } finally {
            one.interrupt();
            two.interrupt();
            one.join(3000);
            two.join(3000);
        }
        assertFalse(one.isAlive());
        assertFalse(two.isAlive());
    }

    /** 持有第一把锁后等待第二把锁，收到中断时释放已持有的所有锁。 */
    private void holdThenAwait(ReentrantLock first, ReentrantLock second, CountDownLatch owned) {
        boolean acquiredFirst = false;
        boolean acquiredSecond = false;
        try {
            first.lockInterruptibly();
            acquiredFirst = true;
            owned.countDown();
            owned.await();
            second.lockInterruptibly();
            acquiredSecond = true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            if (acquiredSecond) {
                second.unlock();
            }
            if (acquiredFirst) {
                first.unlock();
            }
        }
    }

    /** 按真实线程标识定位采样记录。 */
    private ThreadSnapshotThreadVo findThread(ThreadSnapshotVo snapshot, long id) {
        return snapshot.getThreads().stream().filter(thread -> String.valueOf(id).equals(thread.getId()))
                .findFirst().orElseThrow(() -> new AssertionError("采样中缺少测试线程"));
    }

    /** 提供管理接口替身，并记录调用次数以检测重复转储或隐式启用监控。 */
    private static class BeanFixture {
        private final Map<String, Integer> calls = new HashMap<>();
        private boolean cpuSupported = true;
        private boolean cpuEnabled = true;
        private boolean monitorSupported = true;
        private boolean synchronizerSupported = true;
        private boolean dumpDenied;
        private boolean deadlockDenied;
        private boolean cpuDenied;
        private boolean includeNullThread;
        private long cpuNanos = 1500000L;
        private long userNanos = 500000L;
        private long[] deadlockIds;

        /** 创建只替换采集能力的动态代理，其余方法委托真实 JVM。 */
        private ThreadMXBean bean() {
            ThreadMXBean delegate = ManagementFactory.getThreadMXBean();
            return (ThreadMXBean) Proxy.newProxyInstance(ThreadMXBean.class.getClassLoader(),
                    new Class<?>[]{ThreadMXBean.class}, (proxy, method, args) -> {
                        String name = method.getName();
                        calls.merge(name, 1, Integer::sum);
                        switch (name) {
                            case "isThreadCpuTimeSupported": return cpuSupported;
                            case "isThreadCpuTimeEnabled": return cpuEnabled;
                            case "isObjectMonitorUsageSupported": return monitorSupported;
                            case "isSynchronizerUsageSupported": return synchronizerSupported;
                            case "dumpAllThreads":
                                if (dumpDenied) {
                                    throw new UnsupportedOperationException("测试不支持锁转储");
                                }
                                ThreadInfo current = delegate.getThreadInfo(Thread.currentThread().getId(), Integer.MAX_VALUE);
                                if (includeNullThread) {
                                    return new ThreadInfo[]{null, current};
                                }
                                return new ThreadInfo[]{current};
                            case "getThreadInfo":
                                return new ThreadInfo[]{delegate.getThreadInfo(Thread.currentThread().getId(), Integer.MAX_VALUE)};
                            case "findDeadlockedThreads":
                            case "findMonitorDeadlockedThreads":
                                if (deadlockDenied) {
                                    throw new SecurityException("测试无死锁检测权限");
                                }
                                return deadlockIds;
                            case "getThreadCpuTime":
                                if (cpuDenied) {
                                    throw new SecurityException("测试无 CPU 统计权限");
                                }
                                return cpuNanos;
                            case "getThreadUserTime": return userNanos;
                            case "setThreadCpuTimeEnabled":
                            case "setThreadContentionMonitoringEnabled":
                                throw new AssertionError("采集不允许启用监控");
                            default:
                                try {
                                    return method.invoke(delegate, args);
                                } catch (InvocationTargetException e) {
                                    throw e.getCause();
                                }
                        }
                    });
        }

        /** 返回指定方法的调用次数。 */
        private int count(String name) {
            return calls.getOrDefault(name, 0);
        }
    }
}
