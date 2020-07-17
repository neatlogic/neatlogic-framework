package codedriver.framework.restful.counter;

import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.DelayQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import codedriver.framework.asynchronization.thread.CodeDriverThread;
import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.asynchronization.threadpool.CommonThreadPool;
/**
 * 
* @Time:2020年7月17日
* @ClassName: ApiAccessCountManager 
* @Description: 接口访问次数统计管理类
 */
public class ApiAccessCountManager extends CodeDriverThread {
	
	private static Logger logger = LoggerFactory.getLogger(ApiAccessCountManager.class);
	/** 统计延迟对象 **/
	private volatile static DelayedItem delayedItem;
	private volatile static DelayQueue<DelayedItem> delayQueue = new DelayQueue<>();
	
	public static void putToken(String token) {
		System.out.println(token);
		if(delayedItem == null) {
			synchronized(ApiAccessCountManager.class) {
				if(delayedItem == null) {
					delayedItem = new DelayedItem();
					delayQueue.add(delayedItem);
					CommonThreadPool.execute(new ApiAccessCountManager());					
				}
			}			
		}
		delayedItem.putToken(token);
	}

	public ApiAccessCountManager() {
		super.setThreadName("API-ACCESS-COUNTER");
	}

	@Override
	protected void execute() {
		try {
			while(true) {
				DelayedItem take = delayQueue.take();
				System.out.println("take:" + new Date().getTime());
				delayedItem = null;
				for(Entry<String, Map<String, Integer>> entry : take.getTenantAccessTokenMap().entrySet()) {
					TenantContext.init(entry.getKey());
					CommonThreadPool.execute(new ApiAccessCountUpdateThread(entry.getValue()));
				}
			}
		}catch(Exception e) {
			logger.error(e.getMessage(), e);
		}	
	}
}
