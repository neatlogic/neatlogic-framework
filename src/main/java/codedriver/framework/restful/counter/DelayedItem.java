package codedriver.framework.restful.counter;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;

import codedriver.framework.asynchronization.threadlocal.TenantContext;

public class DelayedItem implements Delayed {

	private long delayTime = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(5);
	
	/** 缓存租户访问记录 **/
	private ConcurrentMap<String, Map<String, Integer>> tenantAccessTokenMap = new ConcurrentHashMap<>();
	
	public DelayedItem() {
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
			String tenantUuid = TenantContext.get().getTenantUuid();
			Map<String, Integer> accessTokenCounterMap = tenantAccessTokenMap.get(tenantUuid);
			if(accessTokenCounterMap == null) {
				synchronized(DelayedItem.class){
					accessTokenCounterMap = tenantAccessTokenMap.get(tenantUuid);
					if(accessTokenCounterMap == null) {
						accessTokenCounterMap = new HashMap<>();
						tenantAccessTokenMap.put(tenantUuid, accessTokenCounterMap);
					}
				}
			}
			synchronized(accessTokenCounterMap){
				Integer counter = accessTokenCounterMap.get(token);
				if(counter == null) {
					counter = 0;
				}
				accessTokenCounterMap.put(token, counter + 1);
			}
		}		
	}

	public ConcurrentMap<String, Map<String, Integer>> getTenantAccessTokenMap() {
		return tenantAccessTokenMap;
	}

}
