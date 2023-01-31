/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.framework.message.delay;

import neatlogic.framework.asynchronization.thread.CodeDriverThread;
import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.message.core.IMessageHandler;
import neatlogic.framework.notify.dto.NotifyVo;

public class MessageProducerThread extends CodeDriverThread {

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
