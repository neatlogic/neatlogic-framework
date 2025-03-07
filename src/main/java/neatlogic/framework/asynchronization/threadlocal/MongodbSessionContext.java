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

package neatlogic.framework.asynchronization.threadlocal;

import com.mongodb.client.ClientSession;

public class MongodbSessionContext {
    private ClientSession session;
    // ThreadLocal 存储当前线程的 Session 对象
    private static final ThreadLocal<MongodbSessionContext> sessionThreadLocal = new ThreadLocal<>();

    public static MongodbSessionContext init(ClientSession _session) {
        MongodbSessionContext context = new MongodbSessionContext();
        context.setSession(_session);
        sessionThreadLocal.set(context);
        return context;
    }

    public static MongodbSessionContext get() {
        return sessionThreadLocal.get();
    }

    public ClientSession getSession() {
        return session;
    }

    public void setSession(ClientSession session) {
        this.session = session;
    }

    public void release() {
        sessionThreadLocal.remove();
    }
}
