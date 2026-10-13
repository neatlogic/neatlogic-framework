/*
 *
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the NeatLogic Sustainable Use License (NSUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */

package neatlogic.framework.util;

import neatlogic.framework.asynchronization.threadlocal.RequestContext;
import neatlogic.framework.common.config.Config;
import neatlogic.framework.dto.healthcheck.ThreadSnapshotFrameVo;
import neatlogic.framework.dto.healthcheck.ThreadSnapshotThreadVo;
import neatlogic.framework.dto.healthcheck.ThreadSnapshotVo;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.Writer;
import java.lang.management.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class ThreadUtil {
    // 进程内保持不变，避免服务器编号相同但 JVM 已重启时比较 CPU 累计值。
    private static final String PROCESS_INSTANCE_ID = UuidUtil.randomUuid();

    /**
     * 采集 JVM 线程快照，不自动启用 CPU 或锁竞争时间监控。
     */
    public static ThreadSnapshotVo captureThreadSnapshot() {
        return captureThreadSnapshot(ManagementFactory.getThreadMXBean());
    }

    /**
     * 从指定 JVM 管理接口采集，便于验证不同 JVM 的能力降级行为。
     */
    static ThreadSnapshotVo captureThreadSnapshot(ThreadMXBean mxBean) {
        long startedAt = System.nanoTime();
        ThreadSnapshotVo snapshot = new ThreadSnapshotVo();
        snapshot.setSnapshotId(UuidUtil.randomUuid());
        snapshot.setServerId(Config.SCHEDULE_SERVER_ID);
        snapshot.setProcessInstanceId(PROCESS_INSTANCE_ID);
        snapshot.setCapturedAt(System.currentTimeMillis());
        snapshot.setMonotonicTimeMs(startedAt / 1000000L);
        snapshot.setCpuTimeSupported(mxBean.isThreadCpuTimeSupported());
        if (snapshot.isCpuTimeSupported()) {
            snapshot.setCpuTimeEnabled(mxBean.isThreadCpuTimeEnabled());
            if (!snapshot.isCpuTimeEnabled()) {
                addWarning(snapshot, "CPU_TIME_DISABLED");
            }
        } else {
            addWarning(snapshot, "CPU_TIME_UNSUPPORTED");
        }
        snapshot.setMonitorSupported(mxBean.isObjectMonitorUsageSupported());
        snapshot.setSynchronizerSupported(mxBean.isSynchronizerUsageSupported());
        if (!snapshot.isMonitorSupported()) {
            addWarning(snapshot, "MONITOR_INFORMATION_UNSUPPORTED");
        }
        if (!snapshot.isSynchronizerSupported()) {
            addWarning(snapshot, "SYNCHRONIZER_INFORMATION_UNSUPPORTED");
        }
        ThreadInfo[] infos;
        try {
            // 同一次转储同时获取调用栈和锁，避免独立取栈导致证据错位。
            infos = mxBean.dumpAllThreads(snapshot.isMonitorSupported(), snapshot.isSynchronizerSupported());
        } catch (UnsupportedOperationException | SecurityException e) {
            // 管理接口禁止锁转储时保留普通线程状态与完整栈。
            snapshot.setMonitorSupported(false);
            snapshot.setSynchronizerSupported(false);
            addWarning(snapshot, "LOCK_INFORMATION_UNAVAILABLE");
            infos = mxBean.getThreadInfo(mxBean.getAllThreadIds(), Integer.MAX_VALUE);
        }
        Set<Long> deadlockedIds = new HashSet<>();
        try {
            long[] ids;
            if (snapshot.isSynchronizerSupported()) {
                ids = mxBean.findDeadlockedThreads();
            } else {
                ids = mxBean.findMonitorDeadlockedThreads();
            }
            snapshot.setDeadlockDetectionAvailable(true);
            if (ids != null) {
                for (long id : ids) {
                    deadlockedIds.add(id);
                    snapshot.getDeadlockedThreadIds().add(String.valueOf(id));
                }
            }
        } catch (UnsupportedOperationException | SecurityException e) {
            addWarning(snapshot, "DEADLOCK_DETECTION_UNAVAILABLE");
        }
        for (ThreadInfo info : infos) {
            // 线程可能在转储过程中退出，JVM 会返回空记录。
            if (info == null) {
                continue;
            }
            ThreadSnapshotThreadVo thread = toSnapshotThread(info);
            thread.setDeadlocked(deadlockedIds.contains(info.getThreadId()));
            if (snapshot.isCpuTimeEnabled()) {
                try {
                    thread.setCpuTimeMs(toMilliseconds(mxBean.getThreadCpuTime(info.getThreadId())));
                    thread.setUserTimeMs(toMilliseconds(mxBean.getThreadUserTime(info.getThreadId())));
                } catch (UnsupportedOperationException | SecurityException e) {
                    addWarning(snapshot, "CPU_TIME_UNAVAILABLE");
                }
            }
            snapshot.getThreads().add(thread);
        }
        snapshot.getThreads().sort(Comparator.comparingLong(thread -> Long.parseLong(thread.getId())));
        snapshot.setRawText(toSnapshotText(snapshot));
        snapshot.setCaptureDurationMs((System.nanoTime() - startedAt) / 1000000L);
        return snapshot;
    }

    /** 将 JVM 线程信息投影为可序列化快照，不另行获取线程栈。 */
    private static ThreadSnapshotThreadVo toSnapshotThread(ThreadInfo info) {
        ThreadSnapshotThreadVo thread = new ThreadSnapshotThreadVo();
        thread.setId(String.valueOf(info.getThreadId()));
        thread.setName(info.getThreadName());
        thread.setState(info.getThreadState().name());
        thread.setDaemon(info.isDaemon());
        thread.setPriority(info.getPriority());
        thread.setInNative(info.isInNative());
        thread.setSuspended(info.isSuspended());
        thread.setLockName(info.getLockName());
        if (info.getLockOwnerId() >= 0) {
            thread.setLockOwnerId(String.valueOf(info.getLockOwnerId()));
            thread.setLockOwnerName(info.getLockOwnerName());
        }
        thread.setBlockedCount(info.getBlockedCount());
        thread.setWaitedCount(info.getWaitedCount());
        for (MonitorInfo lock : info.getLockedMonitors()) {
            thread.getOwnedMonitorNames().add(lock.toString());
        }
        for (LockInfo lock : info.getLockedSynchronizers()) {
            thread.getOwnedSynchronizerNames().add(lock.toString());
        }
        for (StackTraceElement element : info.getStackTrace()) {
            ThreadSnapshotFrameVo frame = new ThreadSnapshotFrameVo();
            frame.setClassName(element.getClassName());
            frame.setMethodName(element.getMethodName());
            frame.setFileName(element.getFileName());
            frame.setLineNumber(element.getLineNumber());
            frame.setNativeMethod(element.isNativeMethod());
            frame.setText(element.toString());
            thread.getStackTrace().add(frame);
        }
        return thread;
    }

    /** JVM 对已退出或无法统计的线程返回负值，界面应显示不可用。 */
    private static Double toMilliseconds(long nanoseconds) {
        if (nanoseconds < 0) {
            return null;
        }
        return nanoseconds / 1000000.0;
    }

    /** 每种能力提示在单个快照中仅保留一次。 */
    private static void addWarning(ThreadSnapshotVo snapshot, String code) {
        if (!snapshot.getWarningCodes().contains(code)) {
            snapshot.getWarningCodes().add(code);
        }
    }

    /** 从已经采集的记录生成全文，确保文本和诊断使用同一份证据。 */
    private static String toSnapshotText(ThreadSnapshotVo snapshot) {
        StringBuilder text = new StringBuilder();
        text.append("================= ").append(snapshot.getThreads().size()).append(" threads at ")
                .append(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss z").format(new Date(snapshot.getCapturedAt())))
                .append(" serverId=").append(snapshot.getServerId())
                .append(" processInstanceId=").append(snapshot.getProcessInstanceId()).append(" =================\n\n");
        for (ThreadSnapshotThreadVo thread : snapshot.getThreads()) {
            text.append('"').append(thread.getName()).append("\" prio=").append(thread.getPriority())
                    .append(" tid=").append(thread.getId()).append(' ').append(thread.getState());
            if (thread.isDaemon()) {
                text.append(" daemon");
            } else {
                text.append(" worker");
            }
            text.append(" native=").append(thread.isInNative()).append(", suspended=").append(thread.isSuspended())
                    .append(", block=").append(thread.getBlockedCount()).append(", wait=").append(thread.getWaitedCount())
                    .append(" lock=").append(thread.getLockName()).append(" owned by ").append(thread.getLockOwnerName())
                    .append(" (").append(thread.getLockOwnerId()).append(") cpuMs=").append(thread.getCpuTimeMs())
                    .append(" userMs=").append(thread.getUserTimeMs()).append(" deadlocked=").append(thread.isDeadlocked()).append('\n');
            for (ThreadSnapshotFrameVo frame : thread.getStackTrace()) {
                text.append("\t\tat ").append(frame.getText()).append('\n');
            }
            for (String lock : thread.getOwnedMonitorNames()) {
                text.append("\t- locked monitor ").append(lock).append('\n');
            }
            for (String lock : thread.getOwnedSynchronizerNames()) {
                text.append("\t- locked synchronizer ").append(lock).append('\n');
            }
            text.append('\n');
        }
        text.append("Deadlocked thread IDs: ").append(snapshot.getDeadlockedThreadIds()).append('\n');
        text.append("Collection warnings: ").append(snapshot.getWarningCodes()).append('\n');
        return text.toString();
    }


    public static void dumpTraces(Writer writer) throws IOException {
        ThreadMXBean mxBean = ManagementFactory.getThreadMXBean();
        ThreadInfo[] threadInfos = mxBean.getThreadInfo(mxBean.getAllThreadIds(), 0);
        Map<Long, ThreadInfo> threadInfoMap = new HashMap<>();
        for (ThreadInfo threadInfo : threadInfos) {
            if (threadInfo != null) {
                threadInfoMap.put(threadInfo.getThreadId(), threadInfo);
            }
        }
        Map<Thread, StackTraceElement[]> stacks = Thread.getAllStackTraces();
        long now = System.currentTimeMillis();
        String localAddr = StringUtils.EMPTY;
        String url = StringUtils.EMPTY;
        RequestContext requestContext = RequestContext.get();
        if (requestContext != null) {
            if (requestContext.getRequest() != null && StringUtils.isNotBlank(requestContext.getRequest().getLocalAddr())) {
                localAddr = requestContext.getRequest().getLocalAddr();
            }
            if (StringUtils.isNotBlank(requestContext.getUrl())) {
                url = requestContext.getUrl();
            }
        }
        writer.write("\n=================" + stacks.size() + " thread of " + localAddr + " at " + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss z").format(new Date(now)) + " start.serverId is " + Config.SCHEDULE_SERVER_ID + "=================\n\n");
        for (Map.Entry<Thread, StackTraceElement[]> entry : stacks.entrySet()) {
            Thread thread = entry.getKey();
            writer.write("\"" + thread.getName() + "\" prio=" + thread.getPriority() + " tid=" + thread.getId() + " " + thread.getState() + " " + (thread.isDaemon() ? "deamon" : "worker"));
            ThreadInfo threadInfo = threadInfoMap.get(thread.getId());
            if (threadInfo != null) {
                writer.write(" native=" + threadInfo.isInNative() + ", suspended=" + threadInfo.isSuspended() + ", block=" + threadInfo.getBlockedCount() + ", wait=" + threadInfo.getWaitedCount());
                writer.write(" lock=" + threadInfo.getLockName() + " owned by " + threadInfo.getLockOwnerName() + " (" + threadInfo.getLockOwnerId() + "), cpu=" + (mxBean.getThreadCpuTime(threadInfo.getThreadId()) / 1000000L) + ", user=" + (mxBean.getThreadUserTime(threadInfo.getThreadId()) / 1000000L) + "\n");
            }
            for (StackTraceElement element : entry.getValue()) {
                writer.write("\t\t");
                writer.write(element.toString());
                writer.write("\n");
            }
            writer.write("\n");
        }
        writer.write("=================" + stacks.size() + " thread of " + url + " at " + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss z").format(new Date(now)) + " end.=================\n\n");
    }
}
