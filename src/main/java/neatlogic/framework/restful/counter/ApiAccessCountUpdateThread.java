/*
 * Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package neatlogic.framework.restful.counter;

import neatlogic.framework.asynchronization.thread.NeatLogicThread;
import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.asynchronization.threadpool.CachedThreadPool;
import neatlogic.framework.restful.service.ApiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map.Entry;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Time:2020年7月17日
 * @ClassName: ApiAccessCountManager
 * @Description: 接口访问次数统计管理类
 */
@Service
public class ApiAccessCountUpdateThread extends NeatLogicThread {

    private static final Logger logger = LoggerFactory.getLogger(ApiAccessCountUpdateThread.class);
    /**
     * 统计延迟对象，默认初始化一个失效的延迟对象
     **/
    private volatile static DelayedItem delayedItem = new DelayedItem(true);
    /**
     * 延迟队列
     **/
    private static final DelayQueue<DelayedItem> delayQueue = new DelayQueue<>();

    private static ApiService apiService;

    @Autowired
    public void setApiService(ApiService _apiService) {
        apiService = _apiService;
    }

    public ApiAccessCountUpdateThread() {
        super("API-ACCESS-COUNT-UPDATER");
    }


    public static void putToken(String token) {
        try {
            /* 判断延迟对象是否失效 **/
            if (delayedItem.isExpired()) {
//				Thread.sleep(1);//测试时使用
                /* 初始化延迟对象时，必须加锁，否则会出现多个线程相互覆盖情况 **/
                synchronized (ApiAccessCountUpdateThread.class) {
                    if (delayedItem.isExpired()) {
                        delayedItem = new DelayedItem();
                        delayQueue.add(delayedItem);
                        CachedThreadPool.execute(new ApiAccessCountUpdateThread());
                    }
                }
//				Thread.sleep(1);//测试时使用
                putToken(token);
            } else {
//				Thread.sleep(1);//测试时使用
                delayedItem.putToken(token);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    protected void execute() {
        try {
            DelayedItem take = delayQueue.take();
//			Thread.sleep(1);//测试时使用
            /* 从延迟队列取出延迟对象后，将延迟对象设置为失效，通知其他线程不要再往该对象写数据了 **/
            take.setExpired(true);
//			Thread.sleep(1);//测试时使用
            while (take.getWritingDataThreadNum() > 0) {
                /* 如果还有线程正在往当前延迟对象中写数据 **/
                synchronized (take.getLock()) {
                    /* 等待所有正在往当前延迟对象中写数据的线程完成后，唤醒当前线程 **/
                    take.getLock().wait();
                }
            }

            /**测试代码开始 **/
//			Thread.sleep(10);//测试时使用
//			for(Entry<String, ConcurrentMap<String, AtomicInteger>> tenantAccessTokenEntry : take.getTenantAccessTokenMap().entrySet()) {
//				String tenantUuid = tenantAccessTokenEntry.getKey();
//				for(Entry<String, AtomicInteger> entry : tenantAccessTokenEntry.getValue().entrySet()) {
//					String token = entry.getKey();
//					ConcurrentMap<String, ConcurrentMap<String, AtomicInteger>> tenantAccessTokenMap2 = Test.getTenantAccessTokenMap();
//					ConcurrentMap<String, AtomicInteger> accessTokenCounterMap = tenantAccessTokenMap2.get(tenantUuid);
//					if(accessTokenCounterMap == null) {
//						synchronized(tenantAccessTokenMap2){
//							accessTokenCounterMap = tenantAccessTokenMap2.get(tenantUuid);
//							if(accessTokenCounterMap == null) {
//								accessTokenCounterMap = new ConcurrentHashMap<>();
//								tenantAccessTokenMap2.put(tenantUuid, accessTokenCounterMap);
//							}
//						}
//					}
//					
//					AtomicInteger counter = accessTokenCounterMap.get(token);
//					if(counter == null) {
//						synchronized(accessTokenCounterMap){
//							counter = accessTokenCounterMap.get(token);
//							if(counter == null) {
//								accessTokenCounterMap.put(token, new AtomicInteger(entry.getValue().get()));
//							}else {
//								counter.getAndAdd(entry.getValue().get());
//							}										
//						}
//					}else {
//						counter.getAndAdd(entry.getValue().get());
//					}
//					
//				}
//			}		
            /** 测试代码结束 **/
            /** 业务代码开始**/
            for (Entry<String, ConcurrentMap<String, AtomicInteger>> tenantAccessTokenEntry : take.getTenantAccessTokenMap().entrySet()) {
                TenantContext.init(tenantAccessTokenEntry.getKey());
                for (Entry<String, AtomicInteger> entry : tenantAccessTokenEntry.getValue().entrySet()) {
                    apiService.saveApiAccessCount(entry.getKey(), entry.getValue().get());
                }
            }
            /** 业务代码结束**/
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}
