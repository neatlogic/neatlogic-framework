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
