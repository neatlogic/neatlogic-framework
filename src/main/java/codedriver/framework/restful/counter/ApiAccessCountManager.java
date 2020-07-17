package codedriver.framework.restful.counter;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.DelayQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.asynchronization.threadpool.CommonThreadPool;
/**
 * 
* @Time:2020年7月17日
* @ClassName: ApiAccessCountManager 
* @Description: 接口访问次数统计管理类
 */
public class ApiAccessCountManager {
	
	private static Logger logger = LoggerFactory.getLogger(ApiAccessCountManager.class);
	/** 统计延迟对象 **/
	private volatile static DelayedItem delayedItem = new DelayedItem();
	
	static {		
		Thread thread = new Thread("API-ACCESS-COUNTER") {
			@Override
			public void run() {
				try {
					DelayQueue<DelayedItem> delayQueue = new DelayQueue<>();
					delayQueue.add(delayedItem);
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
	
	public static void putToken(String token) {
		delayedItem.putToken(token);
	}
}
