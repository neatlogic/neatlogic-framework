package codedriver.framework.message.delay;

import codedriver.framework.asynchronization.thread.CodeDriverThread;
import codedriver.framework.asynchronization.threadlocal.TenantContext;

import java.util.concurrent.TimeUnit;

/**
 * @Title: MessageSendThread
 * @Package codedriver.framework.message.delay
 * @Description: TODO
 * @Author: linbq
 * @Date: 2021/1/10 16:45
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 **/
public class MessageSendThread extends CodeDriverThread {

    private MessageCache messageCache;

    public MessageSendThread(MessageCache messageCache){
        super.setThreadName("MESSAGE-SEND-THREAD-" + TenantContext.get().getTenantUuid());
        this.messageCache = messageCache;
    }

    @Override
    protected void execute() {
        while(true){
            long currentTimeMillis = System.currentTimeMillis();
            if(currentTimeMillis >= messageCache.getMaxDelayTime()){
                break;
            }
            if(currentTimeMillis >= messageCache.getMinDelayTime()){
                break;
            }
            try{
                TimeUnit.MILLISECONDS.sleep(messageCache.getMinDelayTime() - currentTimeMillis);
            }catch (InterruptedException e){

            }
        }

    }
}
