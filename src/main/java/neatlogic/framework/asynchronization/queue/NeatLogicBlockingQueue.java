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
import neatlogic.framework.asynchronization.threadlocal.UserContext;

import java.util.concurrent.BlockingQueue;

public class NeatLogicBlockingQueue<T> {

    private final BlockingQueue<QueueTask<T>> blockingQueue;

    public NeatLogicBlockingQueue(BlockingQueue<QueueTask<T>> _blockingQueue) {
        this.blockingQueue = _blockingQueue;
    }

    public boolean offer(T t) {
        return blockingQueue.offer(new QueueTask<>(t));
    }

    public T take() throws InterruptedException {
        QueueTask<T> task = blockingQueue.take();
        TenantContext tenantContext = TenantContext.get();
        UserContext userContext = task.getUserContext();
        if (tenantContext != null) {
            tenantContext.switchTenant(task.getTenantUuid());
        } else {
            TenantContext.init(task.getTenantUuid());
        }
        if (userContext != null) {
            UserContext.init(userContext);
        }
        return task.getT();
    }
}
