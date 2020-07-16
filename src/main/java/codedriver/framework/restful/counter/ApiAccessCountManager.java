package codedriver.framework.restful.counter;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.DelayQueue;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.asynchronization.threadpool.CommonThreadPool;

@Service
public class ApiAccessCountManager {
	
	private static Logger logger = LoggerFactory.getLogger(ApiAccessCountManager.class);
	
	private volatile DelayedItem delayedItem = new DelayedItem();
	
	private DelayQueue<DelayedItem> delayQueue = new DelayQueue<>();
	
	@PostConstruct
	public void init() {
		delayQueue.add(delayedItem);		
		Thread thread = new Thread("API-ACCESS-COUNTER") {
			@Override
			public void run() {
				try {
					while(true) {
						DelayedItem take = delayQueue.take();
						delayedItem = new DelayedItem();
						delayQueue.add(delayedItem);
						for(Entry<String, Map<String, Integer>> entry : take.getTenantAccessTokenMap().entrySet()) {
							TenantContext.init(entry.getKey());
							CommonThreadPool.execute(new ApiAccessCountUpdateThread(entry.getValue()));
						}
					}
				}catch(Exception e) {
					logger.error(e.getMessage(), e);
				}				
			}
		};
		thread.setDaemon(true);
		thread.start();
	}
	public void putToken(String token) {
		delayedItem.putToken(token);
	}
}
