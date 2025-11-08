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
