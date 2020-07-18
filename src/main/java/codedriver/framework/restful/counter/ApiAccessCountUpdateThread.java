package codedriver.framework.restful.counter;

import java.util.Map.Entry;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import codedriver.framework.asynchronization.thread.CodeDriverThread;
import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.asynchronization.threadpool.CommonThreadPool;
import codedriver.framework.restful.service.ApiService;
/**
 * 
* @Time:2020年7月17日
* @ClassName: ApiAccessCountManager 
* @Description: 接口访问次数统计管理类
 */
@Service
public class ApiAccessCountUpdateThread extends CodeDriverThread {
	
	private static Logger logger = LoggerFactory.getLogger(ApiAccessCountUpdateThread.class);
	/** 统计延迟对象，默认初始化一个失效的延迟对象 **/
	private volatile static DelayedItem delayedItem = new DelayedItem(true);
	/** 延迟队列 **/
	private volatile static DelayQueue<DelayedItem> delayQueue = new DelayQueue<>();

	private static ApiService apiService;
	
	@Autowired
	public void setApiService(ApiService _apiService) {
		apiService = _apiService;
	}
	
	public ApiAccessCountUpdateThread() {
		super.setThreadName("API-ACCESS-COUNT-UPDATE");
	}
	
	public static void putToken(String token) {
		/** 判断延迟对象是否失效 **/
		if(delayedItem.isInvalid()) {
			/** 初始化延迟对象时，必须加锁，否则会出现多个线程相互覆盖情况 **/
			synchronized(ApiAccessCountUpdateThread.class) {
				if(delayedItem.isInvalid()) {
					delayedItem = new DelayedItem();
					delayQueue.add(delayedItem);
					CommonThreadPool.execute(new ApiAccessCountUpdateThread());					
				}
			}
			putToken(token);
		}else {
			delayedItem.putToken(token);
		}
	}

	@Override
	protected void execute() {
		try {
			DelayedItem take = delayQueue.take();
			/** 从延迟队列取出延迟对象后，将延迟对象设置为失效，通知其他线程不要再往该对象写数据了 **/
			take.setInvalid(true);
			while(take.getWritingDataThreadNum() > 0) {
				/** 如果还有线程正在往当前延迟对象中写数据 **/
				synchronized(take.getLock()) {
					take.getLock().wait();//等待所有正在往当前延迟对象中写数据的线程完成后，唤醒当前线程
				}
			}

			for(Entry<String, ConcurrentMap<String, AtomicInteger>> tenantAccessTokenEntry : take.getTenantAccessTokenMap().entrySet()) {
				TenantContext.init(tenantAccessTokenEntry.getKey());
				for(Entry<String, AtomicInteger> entry : tenantAccessTokenEntry.getValue().entrySet()) {
					apiService.udpateApiAccessCount(entry.getKey(), entry.getValue().get());
				}
			}
		}catch(Exception e) {
			logger.error(e.getMessage(), e);
		}	
	}
}
