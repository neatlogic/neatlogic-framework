package codedriver.framework.message.delay;

import codedriver.framework.asynchronization.thread.CodeDriverThread;
import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.message.core.IMessageHandler;
import codedriver.framework.notify.dto.NotifyVo;

/**
 * @Title: MessageProducer
 * @Package codedriver.framework.message.delay
 * @Description: 消息缓存生产消息任务类，用于压测
 * @Author: linbq
 * @Date: 2021/1/12 7:24
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 **/
public class MessageProducerThread extends CodeDriverThread {

    private Class<? extends IMessageHandler> messageHandlerClass;

    public MessageProducerThread(Class<? extends IMessageHandler> messageHandlerClass) {
        super.setThreadName("message-producer-thread-" + TenantContext.get().getTenantUuid());
        this.messageHandlerClass = messageHandlerClass;
    }

    public MessageProducerThread() {
        super.setThreadName("message-producer-thread-" + TenantContext.get().getTenantUuid());
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
