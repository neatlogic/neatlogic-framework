package codedriver.framework.restful.counter;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import codedriver.framework.asynchronization.threadpool.CommonThreadPool;
/**
 * 
* @Time:2020年7月19日
* @ClassName: Test 
* @Description: 测试时请将DelayedItem类中的23行注释，25行打开，延迟时间设置为10毫秒，ApiAccessCountUpdateThread类的84-116行打开，119-124行注释
 */
public class Test {
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	/** 缓存租户访问记录 **/	
	private static ConcurrentMap<String, ConcurrentMap<String, AtomicInteger>> tenantAccessTokenMap = new ConcurrentHashMap<>();
	
	/** 租户列表 **/
	private static List<String> tenantUuidList = Arrays.asList("tenant1", "tenant2","tenant3");
	
	/** 接口列表 **/
	private static List<String> tokenList = Arrays.asList("/A", "/B","/C","/D","/E","/F","/G","/H","/I","/J");
	
	/** 每个租户每个token请求次数 **/
	private final static int TENANT_TOKEN_COUNT = 1000;
	
	public static void main(String[] args) throws InterruptedException {
		for(int k = 0; k < 10; k++) {
			long start = System.currentTimeMillis();
			System.out.println("第" + k + "次开始:" + sdf.format(new Date()));
			int num = TENANT_TOKEN_COUNT * tenantUuidList.size() * tokenList.size();
			int tokenIndex = 0;
			for(int i = 0; i < num; i++) {
				Thread.sleep(1);
				String tenantUuid = tenantUuidList.get(i % tenantUuidList.size());
				if(tokenIndex >= tokenList.size()) {
					tokenIndex = 0;
				}
				CommonThreadPool.execute(new ApiAccessCounterThread(i, tenantUuid, tenantUuid + tokenList.get(tokenIndex++)));
			}
			while(true) {
				if(CommonThreadPool.getWorkQueueSize() == 0) {
					/** 队列中没有任务后，延迟100毫秒等待线程执行完毕 **/
					Thread.sleep(100);
					for(Entry<String, ConcurrentMap<String, AtomicInteger>> tenantAccessTokenEntry : tenantAccessTokenMap.entrySet()) {
						String tenantUuid = tenantAccessTokenEntry.getKey();
						for(Entry<String, AtomicInteger> entry : tenantAccessTokenEntry.getValue().entrySet()) {
							String token = entry.getKey();
							int count = entry.getValue().get();
							/** 判断结果是否正确，token以对于的租户uuid开头，统计到的次数与发送请求的次数相同 **/
							if(token.startsWith(tenantUuid) && count == TENANT_TOKEN_COUNT) {
								continue;
							}
							/** 出错才打印统计到的数据 **/
							System.out.println(tenantUuid + ":" + token + "-" + count);
						}
					}
					break;
				}
				Thread.sleep(10);
			}
			
			System.out.println("第" + k + "次结束:" + sdf.format(new Date()));
			long end = System.currentTimeMillis();
			System.out.println("第" + k + "次耗时:" + (end - start) + "毫秒");
			tenantAccessTokenMap = new ConcurrentHashMap<>();
		}
		
	}

	public static ConcurrentMap<String, ConcurrentMap<String, AtomicInteger>> getTenantAccessTokenMap() {
		return tenantAccessTokenMap;
	}

}
