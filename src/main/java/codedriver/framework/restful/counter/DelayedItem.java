package codedriver.framework.restful.counter;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringUtils;

import codedriver.framework.asynchronization.threadlocal.TenantContext;
/**
 * 
* @Time:2020年7月17日
* @ClassName: DelayedItem 
* @Description: 统计次数延迟类
 */
public class DelayedItem implements Delayed {
	/** 延迟5分钟 **/
//	private long delayTime = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(5);
	private long delayTime = System.currentTimeMillis() + TimeUnit.MILLISECONDS.toMillis(10);
	
	/** 缓存租户访问记录 **/
	private ConcurrentMap<String, ConcurrentMap<String, AtomicInteger>> tenantAccessTokenMap = new ConcurrentHashMap<>();
	/** 标记正在往当前延迟对象的缓存tenantAccessTokenMap中写数据的线程数 **/
	private AtomicInteger writingDataThreadNum = new AtomicInteger();
	/** 延迟对象是否失效，未放进延迟队列或已从延迟队列取出的延迟对象会被标记为true **/
	private volatile boolean invalid = false;
	
	private Object lock = new Object();
	
	public DelayedItem() {
	}

	public DelayedItem(boolean invalid) {
		this.invalid = invalid;
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
		if(StringUtils.isNotBlank(token)) {
			writingDataThreadNum.getAndIncrement();
			String tenantUuid = TenantContext.get().getTenantUuid();
			/** 从缓存中获取当前租户访问记录 **/
			ConcurrentMap<String, AtomicInteger> accessTokenCounterMap = tenantAccessTokenMap.get(tenantUuid);
			if(accessTokenCounterMap == null) {
				synchronized(DelayedItem.class){
					accessTokenCounterMap = tenantAccessTokenMap.get(tenantUuid);
					if(accessTokenCounterMap == null) {
						accessTokenCounterMap = new ConcurrentHashMap<>();
						tenantAccessTokenMap.put(tenantUuid, accessTokenCounterMap);
					}
				}
			}
			
			/** 从缓存中获取当前token访问次数 ，并累加1**/
			AtomicInteger counter = accessTokenCounterMap.get(token);
			if(counter == null) {
				synchronized(accessTokenCounterMap){
					counter = accessTokenCounterMap.get(token);
					if(counter == null) {
						accessTokenCounterMap.put(token, new AtomicInteger(1));
					}else {
						counter.getAndIncrement();
					}
				}
			}else {
				counter.getAndIncrement();
			}
			writingDataThreadNum.getAndDecrement();
		}
		if(invalid && writingDataThreadNum.get() == 0) {
			/** 如果当前延迟对象已失效且没有线程往延迟对象写数据，就唤醒lock对象monitor的wait set中的线程，只有一个 **/
			synchronized(lock) {
				lock.notify();
			}
		}
	}

	public ConcurrentMap<String, ConcurrentMap<String, AtomicInteger>> getTenantAccessTokenMap() {
		return tenantAccessTokenMap;
	}

	public int getWritingDataThreadNum() {
		return writingDataThreadNum.get();
	}

	public boolean isInvalid() {
		return invalid;
	}

	public void setInvalid(boolean invalid) {
		this.invalid = invalid;
	}

	public Object getLock() {
		return lock;
	}

}
