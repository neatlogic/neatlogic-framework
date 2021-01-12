package codedriver.framework.message.delay;

import codedriver.framework.asynchronization.thread.CodeDriverThread;
import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.message.dto.MessageVo;
import codedriver.framework.notify.dto.NotifyVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ConcurrentMap;
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

    private static Logger logger = LoggerFactory.getLogger(MessageSendThread.class);

    private MessageCache messageCache;

    public MessageSendThread(MessageCache messageCache){
        super.setThreadName("MESSAGE-SEND-THREAD-" + TenantContext.get().getTenantUuid());
        this.messageCache = messageCache;
    }

    @Override
    protected void execute() {
        System.out.println("MessageSendThread.execute start...");
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
        try{
            messageCache.setExpired(true);
            while(messageCache.getWritingDataThreadNum() > 0) {
                /** 如果还有线程正在往当前延迟对象中写数据 **/
                synchronized(messageCache.getLock()) {
                    /** 等待所有正在往当前延迟对象中写数据的线程完成后，唤醒当前线程 **/
                    messageCache.getLock().wait();
                }
            }
            ConcurrentMap<NotifyVo, Object> notifyVoMap = messageCache.getNotifyVoMap();
            System.out.println(TenantContext.get().getTenantUuid() + ":" + notifyVoMap.size());
        }catch(Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}
