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

package neatlogic.module.framework.restful.counter;

import neatlogic.framework.asynchronization.thread.NeatLogicThread;
import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.restful.dao.mapper.ApiAuditMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class ApiAccessCountServiceImpl implements ApiAccessCountService {

    private static final Logger logger = LoggerFactory.getLogger(ApiAccessCountServiceImpl.class);
    /**
     * 统计延迟对象，默认初始化一个失效的延迟对象
     **/
    private static volatile DelayedItem delayedItem = new DelayedItem(true);
    /**
     * 延迟队列
     **/
    private static final DelayQueue<DelayedItem> delayQueue = new DelayQueue<>();

    @Resource
    private ApiAuditMapper apiAuditMapper;


    @PostConstruct
    public void init() {
        Thread t = new Thread(new NeatLogicThread("API-ACCESS-COUNT-MANAGER") {

            @Override
            protected void execute() {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        DelayedItem take = delayQueue.take();
                        //	Thread.sleep(1);//测试时使用
                        /* 从延迟队列取出延迟对象后，将延迟对象设置为失效，通知其他线程不要再往该对象写数据了 **/
                        take.setExpired(true);
                        //	Thread.sleep(1);//测试时使用
                        while (take.getWritingDataThreadNum() > 0) {
                            /* 如果还有线程正在往当前延迟对象中写数据 **/
                            synchronized (take.getLock()) {
                                /* 等待所有正在往当前延迟对象中写数据的线程完成后，唤醒当前线程 **/
                                take.getLock().wait();
                            }
                        }

                        /**测试代码开始 **/
//                        Thread.sleep(10);//测试时使用
//                        for(Entry<String, ConcurrentMap<String, AtomicInteger>> tenantAccessTokenEntry : take.getTenantAccessTokenMap().entrySet()) {
//                            String tenantUuid = tenantAccessTokenEntry.getKey();
//                            for(Entry<String, AtomicInteger> entry : tenantAccessTokenEntry.getValue().entrySet()) {
//                                String token = entry.getKey();
//                                ConcurrentMap<String, ConcurrentMap<String, AtomicInteger>> tenantAccessTokenMap2 = Test.getTenantAccessTokenMap();
//                                ConcurrentMap<String, AtomicInteger> accessTokenCounterMap = tenantAccessTokenMap2.get(tenantUuid);
//                                if(accessTokenCounterMap == null) {
//                                    synchronized(tenantAccessTokenMap2){
//                                        accessTokenCounterMap = tenantAccessTokenMap2.get(tenantUuid);
//                                        if(accessTokenCounterMap == null) {
//                                            accessTokenCounterMap = new ConcurrentHashMap<>();
//                                            tenantAccessTokenMap2.put(tenantUuid, accessTokenCounterMap);
//                                        }
//                                    }
//                                }
//
//                                AtomicInteger counter = accessTokenCounterMap.get(token);
//                                if(counter == null) {
//                                    synchronized(accessTokenCounterMap){
//                                        counter = accessTokenCounterMap.get(token);
//                                        if(counter == null) {
//                                            accessTokenCounterMap.put(token, new AtomicInteger(entry.getValue().get()));
//                                        }else {
//                                            counter.getAndAdd(entry.getValue().get());
//                                        }
//                                    }
//                                }else {
//                                    counter.getAndAdd(entry.getValue().get());
//                                }
//
//                            }
//                        }
                        /** 测试代码结束 **/
                        /** 业务代码开始**/
                        for (Entry<String, ConcurrentMap<String, AtomicLong>> tenantAccessTokenEntry : take.getTenantAccessTokenMap().entrySet()) {
                            TenantContext.init(tenantAccessTokenEntry.getKey());
                            for (Entry<String, AtomicLong> entry : tenantAccessTokenEntry.getValue().entrySet()) {
                                apiAuditMapper.insertApiAccessCount(entry.getKey(), entry.getValue().get());
                            }
                        }
                        /** 业务代码结束**/
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    }
                }
            }
        });
        t.setDaemon(true);
        t.start();
    }

    @Override
    public void putToken(String token) {
        try {
            /* 判断延迟对象是否失效 **/
            if (!delayedItem.putToken(token)) {
//				Thread.sleep(1);//测试时使用
                /* 初始化延迟对象时，必须加锁，否则会出现多个线程相互覆盖情况 **/
                synchronized (this) {
                    if (delayedItem.isExpired()) {
                        delayedItem = new DelayedItem();
                        delayQueue.add(delayedItem);
                    }
                }
//				Thread.sleep(1);//测试时使用
                delayedItem.putToken(token);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

}
