/*Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.*/

package neatlogic.framework.heartbeat.core;

import neatlogic.framework.asynchronization.thread.NeatLogicThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
