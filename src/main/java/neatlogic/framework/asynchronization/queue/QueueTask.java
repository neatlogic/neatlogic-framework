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

public class QueueTask<T> {
    private final T t;
    private final String tenantUuid;
    private UserContext userContext;

    public QueueTask(T t) {
        this.t = t;
        this.tenantUuid = TenantContext.get().getTenantUuid();
        if (UserContext.get() != null) {
            this.userContext = UserContext.get().copy();
        }
    }

    public T getT() {
        return t;
    }

    public UserContext getUserContext() {
        return userContext;
    }

    public String getTenantUuid() {
        return tenantUuid;
    }

    public String getUniqueKey() {
        // 唯一标识任务的 key，可根据需求定义，例如 `tenantUuid-t.hashCode`
        //System.out.println(tenantUuid + "-" + t.hashCode());
        return tenantUuid + "-" + t.hashCode();
    }
}
