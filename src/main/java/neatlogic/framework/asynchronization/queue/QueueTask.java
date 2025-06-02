/*
 * Copyright (C) 2025  深圳极向量科技有限公司 All Rights Reserved.
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
