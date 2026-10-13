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

package neatlogic.framework.dto.healthcheck;

import java.util.ArrayList;
import java.util.List;

/**
 * 线程快照及采集能力，所有线程信息来自同一次 JVM 转储。
 */
public class ThreadSnapshotVo {
    /** 本次快照的临时标识。 */
    private String snapshotId;
    /** 服务器标识。 */
    private int serverId;
    /** 进程实例标识。 */
    private String processInstanceId;
    /** 实际采样时间，毫秒时间戳。 */
    private long capturedAt;
    /** 单调时间，用于计算采样间隔。 */
    private long monotonicTimeMs;
    /** 采集耗时。 */
    private long captureDurationMs;
    /** 是否支持线程 CPU 统计。 */
    private boolean cpuTimeSupported;
    /** CPU 统计是否已启用。 */
    private boolean cpuTimeEnabled;
    /** 是否支持对象监视器信息。 */
    private boolean monitorSupported;
    /** 是否支持同步器信息。 */
    private boolean synchronizerSupported;
    /** 本次死锁检测是否成功。 */
    private boolean deadlockDetectionAvailable;
    /** 线程记录。 */
    private List<ThreadSnapshotThreadVo> threads = new ArrayList<>();
    /** 死锁线程标识。 */
    private List<String> deadlockedThreadIds = new ArrayList<>();
    /** 采集能力提示码。 */
    private List<String> warningCodes = new ArrayList<>();
    /** 由本次结构化数据生成的原始文本。 */
    private String rawText;
    /** 采集时的线程池状态。 */
    private ThreadPoolVo threadPool;
    /** 当前租户标识。 */
    private String currentTenantUuid;
    /** 单次采样诊断。 */
    private List<ThreadSnapshotDiagnosticVo> diagnostics = new ArrayList<>();

    /** 获取本次快照的临时标识。 */
    public String getSnapshotId() {
        return snapshotId;
    }

    /** 设置本次快照的临时标识。 */
    public void setSnapshotId(String snapshotId) {
        this.snapshotId = snapshotId;
    }

    /** 获取服务器标识。 */
    public int getServerId() {
        return serverId;
    }

    /** 设置服务器标识。 */
    public void setServerId(int serverId) {
        this.serverId = serverId;
    }

    /** 获取进程实例标识。 */
    public String getProcessInstanceId() {
        return processInstanceId;
    }

    /** 设置进程实例标识。 */
    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    /** 获取实际采样时间，毫秒时间戳。 */
    public long getCapturedAt() {
        return capturedAt;
    }

    /** 设置实际采样时间，毫秒时间戳。 */
    public void setCapturedAt(long capturedAt) {
        this.capturedAt = capturedAt;
    }

    /** 获取单调时间，用于计算采样间隔。 */
    public long getMonotonicTimeMs() {
        return monotonicTimeMs;
    }

    /** 设置单调时间，用于计算采样间隔。 */
    public void setMonotonicTimeMs(long monotonicTimeMs) {
        this.monotonicTimeMs = monotonicTimeMs;
    }

    /** 获取采集耗时。 */
    public long getCaptureDurationMs() {
        return captureDurationMs;
    }

    /** 设置采集耗时。 */
    public void setCaptureDurationMs(long captureDurationMs) {
        this.captureDurationMs = captureDurationMs;
    }

    /** 获取是否支持线程 CPU 统计。 */
    public boolean isCpuTimeSupported() {
        return cpuTimeSupported;
    }

    /** 设置是否支持线程 CPU 统计。 */
    public void setCpuTimeSupported(boolean cpuTimeSupported) {
        this.cpuTimeSupported = cpuTimeSupported;
    }

    /** 获取CPU 统计是否已启用。 */
    public boolean isCpuTimeEnabled() {
        return cpuTimeEnabled;
    }

    /** 设置CPU 统计是否已启用。 */
    public void setCpuTimeEnabled(boolean cpuTimeEnabled) {
        this.cpuTimeEnabled = cpuTimeEnabled;
    }

    /** 获取是否支持对象监视器信息。 */
    public boolean isMonitorSupported() {
        return monitorSupported;
    }

    /** 设置是否支持对象监视器信息。 */
    public void setMonitorSupported(boolean monitorSupported) {
        this.monitorSupported = monitorSupported;
    }

    /** 获取是否支持同步器信息。 */
    public boolean isSynchronizerSupported() {
        return synchronizerSupported;
    }

    /** 设置是否支持同步器信息。 */
    public void setSynchronizerSupported(boolean synchronizerSupported) {
        this.synchronizerSupported = synchronizerSupported;
    }

    /** 获取本次死锁检测是否成功。 */
    public boolean isDeadlockDetectionAvailable() {
        return deadlockDetectionAvailable;
    }

    /** 设置本次死锁检测是否成功。 */
    public void setDeadlockDetectionAvailable(boolean deadlockDetectionAvailable) {
        this.deadlockDetectionAvailable = deadlockDetectionAvailable;
    }

    /** 获取线程记录。 */
    public List<ThreadSnapshotThreadVo> getThreads() {
        return threads;
    }

    /** 设置线程记录。 */
    public void setThreads(List<ThreadSnapshotThreadVo> threads) {
        this.threads = threads;
    }

    /** 获取死锁线程标识。 */
    public List<String> getDeadlockedThreadIds() {
        return deadlockedThreadIds;
    }

    /** 设置死锁线程标识。 */
    public void setDeadlockedThreadIds(List<String> deadlockedThreadIds) {
        this.deadlockedThreadIds = deadlockedThreadIds;
    }

    /** 获取采集能力提示码。 */
    public List<String> getWarningCodes() {
        return warningCodes;
    }

    /** 设置采集能力提示码。 */
    public void setWarningCodes(List<String> warningCodes) {
        this.warningCodes = warningCodes;
    }

    /** 获取由本次结构化数据生成的原始文本。 */
    public String getRawText() {
        return rawText;
    }

    /** 设置由本次结构化数据生成的原始文本。 */
    public void setRawText(String rawText) {
        this.rawText = rawText;
    }

    /** 获取采集时的线程池状态。 */
    public ThreadPoolVo getThreadPool() {
        return threadPool;
    }

    /** 设置采集时的线程池状态。 */
    public void setThreadPool(ThreadPoolVo threadPool) {
        this.threadPool = threadPool;
    }

    /** 获取当前租户标识。 */
    public String getCurrentTenantUuid() {
        return currentTenantUuid;
    }

    /** 设置当前租户标识。 */
    public void setCurrentTenantUuid(String currentTenantUuid) {
        this.currentTenantUuid = currentTenantUuid;
    }

    /** 获取单次采样诊断。 */
    public List<ThreadSnapshotDiagnosticVo> getDiagnostics() {
        return diagnostics;
    }

    /** 设置单次采样诊断。 */
    public void setDiagnostics(List<ThreadSnapshotDiagnosticVo> diagnostics) {
        this.diagnostics = diagnostics;
    }
}
