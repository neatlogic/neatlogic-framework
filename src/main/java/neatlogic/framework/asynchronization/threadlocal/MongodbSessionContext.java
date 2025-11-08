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
