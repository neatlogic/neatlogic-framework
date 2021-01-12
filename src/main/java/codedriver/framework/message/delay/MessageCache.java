package codedriver.framework.message.delay;

import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.asynchronization.threadpool.CommonThreadPool;
import codedriver.framework.message.dto.MessageVo;
import codedriver.framework.notify.dto.NotifyVo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.StampedLock;

/**
 * @Title: MessageCache
 * @Package codedriver.framework.message.delay
 * @Description: TODO
 * @Author: linbq
 * @Date: 2021/1/10 16:23
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 **/
public class MessageCache {

    private final ConcurrentMap<NotifyVo, Object> notifyVoMap = new ConcurrentHashMap<>();
    private final int maxDelayDuration = 5 * 60;
    private final int minDelayDuration = 15;
    private final long maxDelayTime;
    private AtomicLong minDelayTime;

    /** 标记正在往当前缓存对象的缓存messageList中写数据的线程数 **/
    private AtomicInteger writingDataThreadNum = new AtomicInteger();
    /** 缓存对象是否失效 **/
    private AtomicBoolean expired = new AtomicBoolean();
    /** 等待缓存对象收集数据完毕锁 **/
    private final Object lock = new Object();

    private MessageCache(NotifyVo messageVo) {
        long currentTimeMillis= System.currentTimeMillis();
        this.maxDelayTime = currentTimeMillis + TimeUnit.SECONDS.toMillis(maxDelayDuration);
        this.minDelayTime.set(currentTimeMillis + TimeUnit.SECONDS.toMillis(minDelayDuration));
        this.notifyVoMap.put(messageVo, null);
        CommonThreadPool.execute(new MessageSendThread(this));
    }

    public ConcurrentMap<NotifyVo, Object> getNotifyVoMap() {
        return notifyVoMap;
    }

    public long getMaxDelayTime() {
        return maxDelayTime;
    }

    public long getMinDelayTime() {
        return this.minDelayTime.get();
    }

    public void updateMinDelayTime() {
        this.minDelayTime.set(System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(minDelayDuration));
    }

    public boolean isExpired() {
        return expired.get();
    }

    public void setExpired(boolean expired) {
        this.expired.set(expired);
    }

    public Object getLock() {
        return lock;
    }

    public int getWritingDataThreadNum() {
        return writingDataThreadNum.get();
    }

    private final static ConcurrentMap<String, MessageCache> messageCacheMap = new ConcurrentHashMap<>();

    public static void add(NotifyVo messageVo){
        MessageCache messageCache = messageCacheMap.get(TenantContext.get().getTenantUuid());
        if(messageCache == null){
            synchronized(MessageCache.class){
                messageCache = messageCacheMap.get(TenantContext.get().getTenantUuid());
                if(messageCache == null){
                    messageCacheMap.put(TenantContext.get().getTenantUuid(), new MessageCache(messageVo));
                    return;
                }
            }
        }
        try{
            messageCache.writingDataThreadNum.incrementAndGet();
            if(messageCache.expired.get()){
                synchronized(MessageCache.class){
                    messageCache = messageCacheMap.get(TenantContext.get().getTenantUuid());
                    if(messageCache.expired.get()){
                        messageCacheMap.put(TenantContext.get().getTenantUuid(), new MessageCache(messageVo));
                        return;
                    }
                }
            }
            messageCache.updateMinDelayTime();
            messageCache.notifyVoMap.put(messageVo, null);
        }finally {
            messageCache.writingDataThreadNum.decrementAndGet();
//			try {
//				Thread.sleep(1);//测试时使用
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
            if(messageCache.writingDataThreadNum.decrementAndGet() <= 0) {
//				try {
//					Thread.sleep(1);//测试时使用
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
                if(messageCache.expired.get()) {
//					try {
//						Thread.sleep(1);//测试时使用
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
                    /** 如果当前缓存对象已失效且没有线程往缓存对象写数据，就唤醒lock对象monitor的wait set中的线程，只有一个 **/
                    synchronized(messageCache.lock) {
                        messageCache.lock.notify();
                    }
                }
            }
        }
    }
}
