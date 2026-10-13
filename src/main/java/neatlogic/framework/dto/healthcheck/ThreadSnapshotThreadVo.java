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
 * 单个 JVM 线程的结构化状态，不以等待状态直接判断故障。
 */
public class ThreadSnapshotThreadVo {
    /** 线程标识。 */
    private String id;
    /** 线程名称。 */
    private String name;
    /** JVM 线程状态。 */
    private String state;
    /** 是否为守护线程。 */
    private boolean daemon;
    /** 线程优先级。 */
    private int priority;
    /** 是否正在执行本地方法。 */
    private boolean inNative;
    /** 线程是否挂起。 */
    private boolean suspended;
    /** 等待的锁。 */
    private String lockName;
    /** 锁持有线程标识，无持有者时为空。 */
    private String lockOwnerId;
    /** 锁持有线程名称。 */
    private String lockOwnerName;
    /** 持有的对象监视器。 */
    private List<String> ownedMonitorNames = new ArrayList<>();
    /** 持有的同步器。 */
    private List<String> ownedSynchronizerNames = new ArrayList<>();
    /** 完整调用栈。 */
    private List<ThreadSnapshotFrameVo> stackTrace = new ArrayList<>();
    /** 累计 CPU 时间，能力不足时为空。 */
    private Double cpuTimeMs;
    /** 累计用户态 CPU 时间，能力不足时为空。 */
    private Double userTimeMs;
    /** 累计阻塞次数。 */
    private long blockedCount;
    /** 累计等待次数。 */
    private long waitedCount;
    /** 采集时关联的业务任务。 */
    private ThreadTaskVo task;
    /** 首个业务栈帧。 */
    private String firstBusinessFrame;
    /** 等待类型提示码。 */
    private String waitType;
    /** 等待类型的调用栈依据。 */
    private String waitEvidence;
    /** 是否属于 JVM 检测出的死锁。 */
    private boolean deadlocked;

    /** 获取线程标识。 */
    public String getId() {
        return id;
    }

    /** 设置线程标识。 */
    public void setId(String id) {
        this.id = id;
    }

    /** 获取线程名称。 */
    public String getName() {
        return name;
    }

    /** 设置线程名称。 */
    public void setName(String name) {
        this.name = name;
    }

    /** 获取JVM 线程状态。 */
    public String getState() {
        return state;
    }

    /** 设置JVM 线程状态。 */
    public void setState(String state) {
        this.state = state;
    }

    /** 获取是否为守护线程。 */
    public boolean isDaemon() {
        return daemon;
    }

    /** 设置是否为守护线程。 */
    public void setDaemon(boolean daemon) {
        this.daemon = daemon;
    }

    /** 获取线程优先级。 */
    public int getPriority() {
        return priority;
    }

    /** 设置线程优先级。 */
    public void setPriority(int priority) {
        this.priority = priority;
    }

    /** 获取是否正在执行本地方法。 */
    public boolean isInNative() {
        return inNative;
    }

    /** 设置是否正在执行本地方法。 */
    public void setInNative(boolean inNative) {
        this.inNative = inNative;
    }

    /** 获取线程是否挂起。 */
    public boolean isSuspended() {
        return suspended;
    }

    /** 设置线程是否挂起。 */
    public void setSuspended(boolean suspended) {
        this.suspended = suspended;
    }

    /** 获取等待的锁。 */
    public String getLockName() {
        return lockName;
    }

    /** 设置等待的锁。 */
    public void setLockName(String lockName) {
        this.lockName = lockName;
    }

    /** 获取锁持有线程标识，无持有者时为空。 */
    public String getLockOwnerId() {
        return lockOwnerId;
    }

    /** 设置锁持有线程标识，无持有者时为空。 */
    public void setLockOwnerId(String lockOwnerId) {
        this.lockOwnerId = lockOwnerId;
    }

    /** 获取锁持有线程名称。 */
    public String getLockOwnerName() {
        return lockOwnerName;
    }

    /** 设置锁持有线程名称。 */
    public void setLockOwnerName(String lockOwnerName) {
        this.lockOwnerName = lockOwnerName;
    }

    /** 获取持有的对象监视器。 */
    public List<String> getOwnedMonitorNames() {
        return ownedMonitorNames;
    }

    /** 设置持有的对象监视器。 */
    public void setOwnedMonitorNames(List<String> ownedMonitorNames) {
        this.ownedMonitorNames = ownedMonitorNames;
    }

    /** 获取持有的同步器。 */
    public List<String> getOwnedSynchronizerNames() {
        return ownedSynchronizerNames;
    }

    /** 设置持有的同步器。 */
    public void setOwnedSynchronizerNames(List<String> ownedSynchronizerNames) {
        this.ownedSynchronizerNames = ownedSynchronizerNames;
    }

    /** 获取完整调用栈。 */
    public List<ThreadSnapshotFrameVo> getStackTrace() {
        return stackTrace;
    }

    /** 设置完整调用栈。 */
    public void setStackTrace(List<ThreadSnapshotFrameVo> stackTrace) {
        this.stackTrace = stackTrace;
    }

    /** 获取累计 CPU 时间，能力不足时为空。 */
    public Double getCpuTimeMs() {
        return cpuTimeMs;
    }

    /** 设置累计 CPU 时间，能力不足时为空。 */
    public void setCpuTimeMs(Double cpuTimeMs) {
        this.cpuTimeMs = cpuTimeMs;
    }

    /** 获取累计用户态 CPU 时间，能力不足时为空。 */
    public Double getUserTimeMs() {
        return userTimeMs;
    }

    /** 设置累计用户态 CPU 时间，能力不足时为空。 */
    public void setUserTimeMs(Double userTimeMs) {
        this.userTimeMs = userTimeMs;
    }

    /** 获取累计阻塞次数。 */
    public long getBlockedCount() {
        return blockedCount;
    }

    /** 设置累计阻塞次数。 */
    public void setBlockedCount(long blockedCount) {
        this.blockedCount = blockedCount;
    }

    /** 获取累计等待次数。 */
    public long getWaitedCount() {
        return waitedCount;
    }

    /** 设置累计等待次数。 */
    public void setWaitedCount(long waitedCount) {
        this.waitedCount = waitedCount;
    }

    /** 获取采集时关联的业务任务。 */
    public ThreadTaskVo getTask() {
        return task;
    }

    /** 设置采集时关联的业务任务。 */
    public void setTask(ThreadTaskVo task) {
        this.task = task;
    }

    /** 获取首个业务栈帧。 */
    public String getFirstBusinessFrame() {
        return firstBusinessFrame;
    }

    /** 设置首个业务栈帧。 */
    public void setFirstBusinessFrame(String firstBusinessFrame) {
        this.firstBusinessFrame = firstBusinessFrame;
    }

    /** 获取等待类型提示码。 */
    public String getWaitType() {
        return waitType;
    }

    /** 设置等待类型提示码。 */
    public void setWaitType(String waitType) {
        this.waitType = waitType;
    }

    /** 获取等待类型的调用栈依据。 */
    public String getWaitEvidence() {
        return waitEvidence;
    }

    /** 设置等待类型的调用栈依据。 */
    public void setWaitEvidence(String waitEvidence) {
        this.waitEvidence = waitEvidence;
    }

    /** 获取是否属于 JVM 检测出的死锁。 */
    public boolean isDeadlocked() {
        return deadlocked;
    }

    /** 设置是否属于 JVM 检测出的死锁。 */
    public void setDeadlocked(boolean deadlocked) {
        this.deadlocked = deadlocked;
    }
}
