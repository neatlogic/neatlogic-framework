/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.message.delay;

import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.asynchronization.threadpool.CachedThreadPool;
import codedriver.framework.notify.dto.NotifyVo;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class MessageCache {
    /**
     * 缓存集合
     **/
    private ConcurrentMap<NotifyVo, Object> notifyVoMap = new ConcurrentHashMap<>();
    /**
     * 最大缓存持续时间，5分钟
     **/
    private final long maxDelayDuration = TimeUnit.MINUTES.toMillis(5);
    /**
     * 最小缓存持续时间，15秒
     **/
    private final long minDelayDuration = TimeUnit.SECONDS.toMillis(15);
    /**
     * 最大缓存时间点，缓存对象创建时就确定了，不会变
     **/
    private final long maxDelayTime;
    /**
     * 最小缓存时间点，每次加入缓存数据都会改变这个值，新值 minDelayTime = System.currentTimeMillis() + minDelayDuration
     **/
    private AtomicLong minDelayTime;

    /**
     * 标记正在往当前缓存对象的缓存notifyVoMap中写数据的线程数
     **/
    private AtomicInteger writingDataThreadNum = new AtomicInteger();
    /**
     * 缓存对象是否失效
     **/
    private AtomicBoolean expired = new AtomicBoolean();
    /**
     * 等待缓存对象收集数据完毕锁
     **/
    private final Object LOCK = new Object();

    private MessageCache(NotifyVo notifyVo) {
        long currentTimeMillis = System.currentTimeMillis();
        this.maxDelayTime = currentTimeMillis + maxDelayDuration;
        this.minDelayTime = new AtomicLong(currentTimeMillis + minDelayDuration);
        this.notifyVoMap.put(notifyVo, NOT_NULL);
        /* 每次创建缓存对象时，创建一个任务监听缓存对象 **/
        CachedThreadPool.execute(new MessageSendThread(this));
    }

    public ConcurrentMap<NotifyVo, Object> getNotifyVoMap() {
        return notifyVoMap;
    }

    public void setNotifyVoMap(ConcurrentMap<NotifyVo, Object> notifyVoMap) {
        this.notifyVoMap = notifyVoMap;
    }

    public long getMaxDelayTime() {
        return maxDelayTime;
    }

    public long getMinDelayTime() {
        return this.minDelayTime.get();
    }

    public void updateMinDelayTime() {
        this.minDelayTime.set(System.currentTimeMillis() + minDelayDuration);
    }

    public void setExpired(boolean expired) {
        this.expired.set(expired);
    }

    public Object getLOCK() {
        return LOCK;
    }

    public int getWritingDataThreadNum() {
        return writingDataThreadNum.get();
    }

    private final static Object NOT_NULL = new Object();

    private final static ConcurrentMap<String, MessageCache> messageCacheMap = new ConcurrentHashMap<>();

    /**
     * @Description: 向缓存对象中添加数据
     * @Author: linbq
     * @Date: 2021/1/12 18:12
     * @Params:[notifyVo]
     * @Returns:void
     **/
    public static void add(NotifyVo notifyVo) {
        MessageCache messageCache = messageCacheMap.get(TenantContext.get().getTenantUuid());
        /** 如果当前租户没有对应的缓存对象，则创建一个新的缓存对象 **/
        if (messageCache == null) {
            synchronized (MessageCache.class) {
                messageCache = messageCacheMap.get(TenantContext.get().getTenantUuid());
                if (messageCache == null) {
                    messageCacheMap.put(TenantContext.get().getTenantUuid(), new MessageCache(notifyVo));
                    return;
                }
            }
        }
        try {
            messageCache.writingDataThreadNum.incrementAndGet();
            if (messageCache.expired.get()) {
                MessageCache.add(notifyVo);
            } else {
                /** 更新最小缓存时间点 **/
                messageCache.updateMinDelayTime();
                /** 将数据存放到缓存对象 **/
                messageCache.notifyVoMap.put(notifyVo, NOT_NULL);
            }
        } finally {
            if (messageCache.writingDataThreadNum.decrementAndGet() <= 0) {
                if (messageCache.expired.get()) {
                    /** 如果当前缓存对象已失效且没有线程往缓存对象写数据，就唤醒lock对象monitor的wait set中的线程，只有一个 **/
                    synchronized (messageCache.LOCK) {
                        messageCache.LOCK.notify();
                    }
                }
            }
        }
    }

    /**
     * @Description: 当缓存对象失效时调用，清除当前租户消息缓存
     * @Author: linbq
     * @Date: 2021/1/12 18:11
     * @Params:[]
     * @Returns:void
     **/
    public static void clear() {
        messageCacheMap.remove(TenantContext.get().getTenantUuid());
    }
}
