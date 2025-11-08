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

import neatlogic.framework.common.config.Config;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class ThreadPoolVo {
    private int mainQueueSize;
    private int mainPoolSize;
    private int backupQueueSize;
    private int backupPoolSize;
    private int mainActiveCount;
    private int backupActiveCount;
    private int maxThreadCount;
    private List<ThreadTaskVo> threadTaskList = new ArrayList<>();
    private List<ThreadVo> threadList = new ArrayList<>();
    private int serverId;

    public int getMaxThreadCount() {
        return maxThreadCount;
    }

    public void setMaxThreadCount(int maxThreadCount) {
        this.maxThreadCount = maxThreadCount;
    }

    public int getServerId() {
        return Config.SCHEDULE_SERVER_ID;
    }

    public int getMainQueueSize() {
        return mainQueueSize;
    }

    public void setMainQueueSize(int mainQueueSize) {
        this.mainQueueSize = mainQueueSize;
    }

    public int getMainPoolSize() {
        return mainPoolSize;
    }

    public int getMainActiveCount() {
        return mainActiveCount;
    }

    public void setMainActiveCount(int mainActiveCount) {
        this.mainActiveCount = mainActiveCount;
    }

    public int getBackupActiveCount() {
        return backupActiveCount;
    }

    public void setBackupActiveCount(int backupActiveCount) {
        this.backupActiveCount = backupActiveCount;
    }

    public void setMainPoolSize(int mainPoolSize) {
        this.mainPoolSize = mainPoolSize;
    }

    public int getBackupQueueSize() {
        return backupQueueSize;
    }

    public void setBackupQueueSize(int backupQueueSize) {
        this.backupQueueSize = backupQueueSize;
    }

    public int getBackupPoolSize() {
        return backupPoolSize;
    }

    public void setBackupPoolSize(int backupPoolSize) {
        this.backupPoolSize = backupPoolSize;
    }

    boolean isSorted = false;

    public List<ThreadTaskVo> getThreadTaskList() {
        if (CollectionUtils.isNotEmpty(threadTaskList)) {
            threadTaskList.sort((o1, o2) -> {
                long s = o1.getStartTime().getTime();
                long e = o2.getStartTime().getTime();
                return Long.compare(s, e);
            });
        }
        return threadTaskList;
    }

    public void setThreadTaskList(List<ThreadTaskVo> threadTaskList) {
        this.threadTaskList = threadTaskList;
    }

    public List<ThreadVo> getThreadList() {
        if (CollectionUtils.isNotEmpty(threadList)) {
            threadList.sort((o1, o2) -> {
                long s = o1.getStartTime().getTime();
                long e = o2.getStartTime().getTime();
                return Long.compare(s, e);
            });
        }
        return threadList;
    }

    public void setThreadList(List<ThreadVo> threadList) {
        this.threadList = threadList;
    }
}
