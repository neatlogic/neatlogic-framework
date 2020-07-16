package codedriver.framework.restful.counter;

import java.util.concurrent.DelayQueue;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ApiAccessCountManager {
	
	private static Logger logger = LoggerFactory.getLogger(ApiAccessCountManager.class);
	
	private DelayedItem delayedItem = new DelayedItem();
	
	private DelayQueue<DelayedItem> delayQueue = new DelayQueue<>();
	
	@PostConstruct
	public void init() {
		//item = new Item();
		delayQueue.add(delayedItem);
		Thread thread = new Thread("API-ACCESS-Count") {
			@Override
			public void run() {
				try {
					DelayedItem take = null;
					while((take = delayQueue.take()) != null) {
						delayedItem = new DelayedItem();
						delayQueue.add(delayedItem);
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
