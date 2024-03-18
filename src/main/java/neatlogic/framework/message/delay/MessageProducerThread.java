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

package neatlogic.framework.message.delay;

import neatlogic.framework.asynchronization.thread.NeatLogicThread;
import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.message.core.IMessageHandler;
import neatlogic.framework.notify.dto.NotifyVo;

public class MessageProducerThread extends NeatLogicThread {

    private Class<? extends IMessageHandler> messageHandlerClass;

    public MessageProducerThread(Class<? extends IMessageHandler> messageHandlerClass) {
        super("MESSAGE-PRODUCER-" + TenantContext.get().getTenantUuid());
        this.messageHandlerClass = messageHandlerClass;
    }

    public MessageProducerThread() {
        super("MESSAGE-PRODUCER-" + TenantContext.get().getTenantUuid());
    }

    @Override
    protected void execute() {
        MessageCache.add(getNotifyVo());
    }

    private NotifyVo getNotifyVo() {
        NotifyVo.Builder builder = new NotifyVo.Builder(TestNotifyTriggerType.TEST, TestMessageHandler.class);
//        builder.withTenantUuid(TenantContext.get().getTenantUuid());
        return builder.build();
    }
}
