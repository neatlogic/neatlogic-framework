package codedriver.framework.message.delay;

import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.asynchronization.threadpool.CommonThreadPool;
import codedriver.framework.message.dto.MessageVo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
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

    private final List<MessageVo> messageList = new ArrayList<>();
    private final int maxDelayDuration = 5 * 60;
    private final int minDelayDuration = 15;
    private final long maxDelayTime;
    private long minDelayTime;

    /** 标记正在往当前缓存对象的缓存messageList中写数据的线程数 **/
    private AtomicInteger writingDataThreadNum = new AtomicInteger();
    /** 缓存对象是否失效 **/
    private AtomicBoolean expired = new AtomicBoolean();
    /** 等待缓存对象收集数据完毕锁 **/
    private Object lock = new Object();

    private MessageCache(MessageVo messageVo) {
        long currentTimeMillis= System.currentTimeMillis();
        this.maxDelayTime = currentTimeMillis + TimeUnit.SECONDS.toMillis(maxDelayDuration);
        this.minDelayTime = currentTimeMillis + TimeUnit.SECONDS.toMillis(minDelayDuration);
        this.messageList.add(messageVo);
        CommonThreadPool.execute(new MessageSendThread(this));
    }

    public List<MessageVo> getMessageList() {
        return messageList;
    }

    public int getMaxDelayDuration() {
        return maxDelayDuration;
    }

    public int getMinDelayDuration() {
        return minDelayDuration;
    }

    public long getMaxDelayTime() {
        return maxDelayTime;
    }

    public long getMinDelayTime() {
        return minDelayTime;
    }

    public void setMinDelayTime(long minDelayTime) {
        this.minDelayTime = minDelayTime;
    }

    public boolean addMessage(MessageVo messageVo){
        return this.messageList.add(messageVo);
    }

    private final static ConcurrentMap<String, MessageCache> messageCacheMap = new ConcurrentHashMap<>();

    public static boolean add(MessageVo messageVo){
        MessageCache messageCache = messageCacheMap.get(TenantContext.get().getTenantUuid());
        if(messageCache == null){
            synchronized(MessageCache.class){
                messageCache = messageCacheMap.get(TenantContext.get().getTenantUuid());
                if(messageCache == null){
                    messageCacheMap.put(TenantContext.get().getTenantUuid(), new MessageCache(messageVo));
                    return true;
                }
            }
        }
        return messageCache.addMessage(messageVo);
    }

    public static MessageCache getMessageCache(){
        return messageCacheMap.get(TenantContext.get().getTenantUuid());
    }

    public static MessageCache putMessageCache(MessageCache messageCache){
        return messageCacheMap.put(TenantContext.get().getTenantUuid(), messageCache);
    }
}
