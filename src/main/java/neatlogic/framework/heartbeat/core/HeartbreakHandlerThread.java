/*
 * Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package neatlogic.framework.heartbeat.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import neatlogic.framework.asynchronization.thread.NeatLogicThread;

public class HeartbreakHandlerThread extends NeatLogicThread {
    private final Logger logger = LoggerFactory.getLogger(HeartbreakHandlerThread.class);
    private final IHeartbreakHandler observer;
    private final Integer serverId;

    public HeartbreakHandlerThread(IHeartbreakHandler observer, Integer serverId) {
        super("HEARTBREAK-HANDLER");
        this.observer = observer;
        this.serverId = serverId;
    }

    @Override
    protected void execute() {
        String oldThreadName = Thread.currentThread().getName();
        try {
            Thread.currentThread().setName("HEARTBREAK-HANDLER");
            observer.whenServerInactivated(serverId);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            Thread.currentThread().setName(oldThreadName);
        }
    }

}
