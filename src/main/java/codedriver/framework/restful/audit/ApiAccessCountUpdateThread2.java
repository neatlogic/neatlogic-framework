package codedriver.framework.restful.audit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.yarn.util.timeline.TimelineUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 
* @Time:2020年7月15日
* @ClassName: ApiAccessCountUpdateThread 
* @Description: 接口访问次数统计任务类
 */
public class ApiAccessCountUpdateThread2 implements Runnable {
	
	private static  Logger logger = LoggerFactory.getLogger(ApiAccessCountUpdateThread2.class);
	/** 缓存访问次数阈值，同个租户访问次数达到阈值后写入数据库 **/
	private final static int ACCESS_COUNT_THRESHOLD = 1000;
	/** 缓存租户访问记录 **/
	private static ConcurrentMap<String, List<String>> tenantAccessTokenMap = new ConcurrentHashMap<>();
	
	private String token;
	
	private String tenantUuid;
	
	public ApiAccessCountUpdateThread2(String tenantUuid, String token) {
		this.token = token;
		this.tenantUuid = tenantUuid;
	}

	public ApiAccessCountUpdateThread2() {
	}

	@Override
	public void run() {
		if(StringUtils.isNotBlank(token) && StringUtils.isNotBlank(tenantUuid)) {
			/** 从缓存中获取当前租户访问记录**/
			List<String> accessTokenList = tenantAccessTokenMap.get(tenantUuid);
			if(accessTokenList == null) {
				/** 首次访问初始化缓存 **/
				synchronized(ApiAccessCountUpdateThread2.class) {
					accessTokenList = tenantAccessTokenMap.get(tenantUuid);
					if(accessTokenList == null) {
						accessTokenList = new ArrayList<>(ACCESS_COUNT_THRESHOLD);
						tenantAccessTokenMap.put(tenantUuid, accessTokenList);
					}
				}
			}
			List<String> list = new ArrayList<>();
			synchronized(accessTokenList) {
				accessTokenList.add(token);
				/** 累计达到缓存次数阈值时，写入数据库并清空缓存 **/
				if(accessTokenList.size() == ACCESS_COUNT_THRESHOLD) {
					list.addAll(accessTokenList);
					accessTokenList.clear();
				}
			}
			if(CollectionUtils.isNotEmpty(list)) {
				//System.out.println(tenantUuid + "-start .............");
				/** 统计每个接口的访问次数 **/
				Map<String, Integer> tokenAccessCountMap = new HashMap<>();
				for(String token : list) {
					Integer count = tokenAccessCountMap.get(token);
					if(count == null) {
						tokenAccessCountMap.put(token, 1);
					}else {
						tokenAccessCountMap.put(token, count + 1);
					}
				}
				for(Entry<String, Integer> entry : tokenAccessCountMap.entrySet()) {
					if(!entry.getKey().startsWith(tenantUuid)) {
						System.out.println(tenantUuid + ":" + entry.getKey() + ":" + entry.getValue());
					}
				}
				//System.out.println(tenantUuid + "-end .............");
			}
		}		
	}
	
	public static void main(String[] args) {
		String[] tenantList = {"tenant1", "tenant2","tenant3","tenant4","tenant5","tenant6","tenant7","tenant8","tenant9","tenant10"};
		String[] tokenList = {"A", "B","C","D","E","F","G","H","I","J"};
		for(int i = 0; i < tenantList.length; i++) {
			for(int j = 0; j < 10000; j++) {
//				Thread t = new Thread(new ApiAccessCountUpdateThread2(tenantList[i], tenantList[i] + tokenList[j%10]));
//				t.start();
//				try {
////					TimeUnit.SECONDS.sleep(1);
////					TimeUnit.MILLISECONDS.sleep(1);
//					TimeUnit.MICROSECONDS.sleep(1);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
				CommonThreadPool.execute(new ApiAccessCountUpdateThread2(tenantList[i], tenantList[i] + tokenList[j%10]));
			}
		}
	}
}
