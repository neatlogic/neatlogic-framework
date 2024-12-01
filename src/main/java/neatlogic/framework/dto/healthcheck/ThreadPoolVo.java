/*
 * Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.
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

package neatlogic.framework.dto.healthcheck;

import neatlogic.framework.common.config.Config;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public class ThreadPoolVo {
    private int mainQueueSize;
    private int mainPoolSize;
    private int backupQueueSize;
    private int backupPoolSize;
    private int mainActiveCount;
    private int backupActiveCount;
    private List<ThreadVo> threadList;
    private int serverId;

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

    public List<ThreadVo> getThreadList() {
        if (CollectionUtils.isNotEmpty(threadList) && !isSorted) {
            threadList.sort((o1, o2) -> {
                long s = o1.getStartTime().getTime();
                long e = o2.getStartTime().getTime();
                return Long.compare(s, e);
            });
            isSorted = true;
        }
        return threadList;
    }

    public void setThreadList(List<ThreadVo> threadList) {
        this.threadList = threadList;
    }
}
