package codedriver.framework.message.delay;

import codedriver.framework.asynchronization.thread.CodeDriverThread;
import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.message.core.IMessageHandler;
import codedriver.framework.message.dto.MessageVo;
import codedriver.framework.notify.core.INotifyTriggerType;
import codedriver.framework.notify.dto.NotifyVo;

/**
 * @Title: MessageProducer
 * @Package codedriver.framework.message.delay
 * @Description: TODO
 * @Author: linbq
 * @Date: 2021/1/12 7:24
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 **/
public class MessageProducerThread extends CodeDriverThread {

    private Class<? extends IMessageHandler> messageHandlerClass;

    public MessageProducerThread(Class<? extends IMessageHandler> messageHandlerClass){
        super.setThreadName("message-producer-thread-" + TenantContext.get().getTenantUuid());
        this.messageHandlerClass = messageHandlerClass;
    }

    public MessageProducerThread(){
        super.setThreadName("message-producer-thread-" + TenantContext.get().getTenantUuid());
    }

    @Override
    protected void execute() {
        System.out.println("MessageProducerThread.execute start...");
        MessageCache.add(getNotifyVo());
        System.out.println("MessageProducerThread.execute end...");
    }

    private NotifyVo getNotifyVo(){
        NotifyVo.Builder builder = new NotifyVo.Builder(TestNotifyTriggerType.TEST, TestMessageHandler.class);
        return builder.build();
    }
}
