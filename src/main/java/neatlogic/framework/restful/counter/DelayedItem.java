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

import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Time:2020年7月17日
 * @ClassName: DelayedItem
 * @Description: 统计次数延迟类
 */
public class DelayedItem implements Delayed {
	private static final Logger logger = LoggerFactory.getLogger(DelayedItem.class);
	/**
	 * 延迟5分钟
	 **/
	private final long delayTime = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(5);
	/* 延迟10毫秒，测试时使用**/
//	private long delayTime = System.currentTimeMillis() + TimeUnit.MILLISECONDS.toMillis(10);

	/**
	 * 缓存租户访问记录
	 **/
	private final ConcurrentMap<String, ConcurrentMap<String, AtomicInteger>> tenantAccessTokenMap = new ConcurrentHashMap<>();
	/**
	 * 标记正在往当前延迟对象的缓存tenantAccessTokenMap中写数据的线程数
	 **/
	private final AtomicInteger writingDataThreadNum = new AtomicInteger();
	/**
	 * 延迟对象是否失效，未放进延迟队列或已从延迟队列取出的延迟对象会被标记为true
	 **/
	private final AtomicBoolean expired = new AtomicBoolean();
	/**
	 * 等待延迟对象收集数据完毕锁
	 **/
	private final Object lock = new Object();

	public DelayedItem() {

	}

	public DelayedItem(boolean expired) {
		this.expired.set(expired);
	}

	@Override
	public int compareTo(Delayed o) {
		DelayedItem item = (DelayedItem) o;
		return Long.compare(this.delayTime, item.delayTime);
	}

	@Override
	public long getDelay(TimeUnit unit) {
		return delayTime - System.currentTimeMillis();
	}

	public void putToken(String token) {
		try {
			/* 写数据前加1**/
			writingDataThreadNum.incrementAndGet();
//			Thread.sleep(1);//测试时使用
			/* 判断延迟对象是否失效 **/
			if(expired.get()) {
//				Thread.sleep(1);//测试时使用
				ApiAccessCountUpdateThread.putToken(token);				
			}else {
				String tenantUuid = TenantContext.get().getTenantUuid();
				/* 从缓存中获取当前租户访问记录 **/
				ConcurrentMap<String, AtomicInteger> accessTokenCounterMap = tenantAccessTokenMap.get(tenantUuid);
				if (accessTokenCounterMap == null) {
					/* 初始化某个租户访问记录缓存时，必须加锁，否则会出现多个线程相互覆盖情况 **/
					synchronized (this) {
						accessTokenCounterMap = tenantAccessTokenMap.get(tenantUuid);
						if (accessTokenCounterMap == null) {
							accessTokenCounterMap = new ConcurrentHashMap<>();
							tenantAccessTokenMap.put(tenantUuid, accessTokenCounterMap);
						}
					}
				}

				/* 从缓存中获取当前token访问次数 ，并累加1**/
				AtomicInteger counter = accessTokenCounterMap.get(token);
				if (counter == null) {
					/* 初始化某个token访问次数时，必须加锁，否则会出现多个线程相互覆盖情况 **/
					synchronized (accessTokenCounterMap) {
						counter = accessTokenCounterMap.get(token);
						if (counter == null) {
							accessTokenCounterMap.put(token, new AtomicInteger(1));
						} else {
							counter.incrementAndGet();
						}
					}
				}else {
//					Thread.sleep(1);//测试时使用
					counter.incrementAndGet();
				}
			}
			
		}catch(Exception e) {
			logger.error(e.getMessage(), e);
		}finally {
//			try {
//				Thread.sleep(1);//测试时使用
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
			if(writingDataThreadNum.decrementAndGet() <= 0) {
//				try {
//					Thread.sleep(1);//测试时使用
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
				if(expired.get()) {
//					try {
//						Thread.sleep(1);//测试时使用
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
					/* 如果当前延迟对象已失效且没有线程往延迟对象写数据，就唤醒lock对象monitor的wait set中的线程，只有一个 **/
					synchronized(lock) {
						lock.notify();
					}
				}
			}		
		}
	}

	public ConcurrentMap<String, ConcurrentMap<String, AtomicInteger>> getTenantAccessTokenMap() {
		return tenantAccessTokenMap;
	}

	public int getWritingDataThreadNum() {
		return writingDataThreadNum.get();
	}

	public boolean isExpired() {
		return expired.get();
	}

	public void setExpired(boolean expired) {
		this.expired.set(expired);;
	}

	public Object getLock() {
		return lock;
	}

}
