package codedriver.framework.message.delay;

import codedriver.framework.asynchronization.thread.CodeDriverThread;
import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.notify.dto.NotifyVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

/**
 * @Title: MessageSendThread
 * @Package codedriver.framework.message.delay
 * @Description: 监听缓存对象的任务
 * @Author: linbq
 * @Date: 2021/1/10 16:45
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 **/
public class MessageSendThread extends CodeDriverThread {

    private static Logger logger = LoggerFactory.getLogger(MessageSendThread.class);

    private MessageCache messageCache;

    public MessageSendThread(MessageCache messageCache) {
        super.setThreadName("MESSAGE-SEND-THREAD-" + TenantContext.get().getTenantUuid());
        this.messageCache = messageCache;
    }

    @Override
    protected void execute() {
        while (true) {
            long currentTimeMillis = System.currentTimeMillis();
            /** 如果当前时间大于最大延迟时间，则开始发送信息 **/
            if (currentTimeMillis >= messageCache.getMaxDelayTime()) {
                break;
            }
            /** 如果当前时间大于最小延迟时间，则开始发送信息 **/
            if (currentTimeMillis >= messageCache.getMinDelayTime()) {
                break;
            }
            /** 睡眠到最小的延迟时间 **/
            try {
                TimeUnit.MILLISECONDS.sleep(messageCache.getMinDelayTime() - currentTimeMillis);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            MessageCache.clear();
            /** 将缓存对象设置为失效 **/
            messageCache.setExpired(true);
            while (messageCache.getWritingDataThreadNum() > 0) {
                /** 如果还有线程正在往当前缓存对象中写数据 **/
                synchronized (messageCache.getLOCK()) {
                    /** 等待所有正在往当前缓存对象中写数据的线程完成后，唤醒当前线程 **/
                    messageCache.getLOCK().wait();
                }
            }
            ConcurrentMap<NotifyVo, Object> notifyVoMap = messageCache.getNotifyVoMap();
            /** 开始处理消息 **/
//            System.out.println(TenantContext.get().getTenantUuid() + ":" + notifyVoMap.size());
//            Test.putAllNotifyVoMap(notifyVoMap);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}
