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

package neatlogic.framework.asynchronization.queue;

import neatlogic.framework.asynchronization.threadlocal.TenantContext;

import java.util.concurrent.BlockingQueue;

public class NeatLogicBlockingQueue<T> {

    private final BlockingQueue<Task<T>> blockingQueue;

    public NeatLogicBlockingQueue(BlockingQueue<Task<T>> _blockingQueue) {
        this.blockingQueue = _blockingQueue;
    }

    public boolean offer(T id) {
        return blockingQueue.offer(new Task<>(id));
    }

    public T take() throws InterruptedException {
        Task<T> task = blockingQueue.take();
        TenantContext tenantContext = TenantContext.get();
        if (tenantContext != null) {
            tenantContext.switchTenant(task.getTenantUuid());
        } else {
            TenantContext.init(task.getTenantUuid());
        }
        return task.getT();
    }

    private static class Task<T> {
        private final T t;
        private final String tenantUuid;

        public Task(T t) {
            this.t = t;
            this.tenantUuid = TenantContext.get().getTenantUuid();
        }

        public T getT() {
            return t;
        }

        public String getTenantUuid() {
            return tenantUuid;
        }
    }
}
