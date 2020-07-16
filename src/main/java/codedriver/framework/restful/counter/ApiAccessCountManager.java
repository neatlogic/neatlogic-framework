package codedriver.framework.restful.counter;

import java.text.SimpleDateFormat;
import java.util.Date;
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
//		try {
//			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
//			System.out.println("begin time: " + sdf.format(new Date()));
//			DelayedItem take = delayQueue.take();
//			System.out.println("name:" + take.getClass() + "-" + sdf.format(new Date()));
//		} catch (InterruptedException e1) {
//			e1.printStackTrace();
//		}
		Thread thread = new Thread("API-ACCESS-COUNTER") {
			@Override
			public void run() {
				try {
					System.out.println("++++++++++++++++++++++++++");
					DelayedItem take = null;
					while((take = delayQueue.take()) != null) {
						System.out.println("-----------------------------------------------");
						delayedItem = new DelayedItem();
						delayQueue.add(delayedItem);
						for(Entry<String, Map<String, Integer>> entry : take.getTenantAccessTokenMap().entrySet()) {
							TenantContext.init(entry.getKey());
							CommonThreadPool.execute(new ApiAccessCountUpdateThread(entry.getValue()));
						}
					}
					System.out.println("***************************");
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
