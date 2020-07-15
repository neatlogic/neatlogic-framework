package codedriver.framework.restful.audit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;

import codedriver.framework.asynchronization.thread.CodeDriverThread;
import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.restful.dao.mapper.ApiMapper;
import codedriver.framework.transaction.util.TransactionUtil;
/**
 * 
* @Time:2020年7月15日
* @ClassName: ApiAccessCountUpdateThread 
* @Description: 接口访问次数统计任务类
 */
@Service
public class ApiAccessCountUpdateThread extends CodeDriverThread {
	
	private static  Logger logger = LoggerFactory.getLogger(ApiAccessCountUpdateThread.class);
	/** 缓存访问次数阈值，同个租户访问次数达到阈值后写入数据库 **/
	private final static int ACCESS_COUNT_THRESHOLD = 1000;
	
	private static ApiMapper apiMapper;

	private static TransactionUtil transactionUtil;
	/** 缓存租户访问记录 **/
	private static ConcurrentMap<String, List<String>> tenantAccessTokenMap = new ConcurrentHashMap<>();
	
	private String token;
	
	@Autowired
	public void setApiMapper(ApiMapper _apiMapper) {
		apiMapper = _apiMapper;
	}

	@Autowired
	public void setTransactionUtil(TransactionUtil _transactionUtil) {
		transactionUtil = _transactionUtil;
	}
	public ApiAccessCountUpdateThread(String token) {
		this.token = token;
	}

	public ApiAccessCountUpdateThread() {
	}

	@Override
	protected void execute() {
		String oldThreadName = Thread.currentThread().getName();
		try {
			String tenantUuid = TenantContext.get().getTenantUuid();
			Thread.currentThread().setName("API-ACCESS-COUNT-" + tenantUuid);
			if(StringUtils.isNotBlank(token)) {
				/** 从缓存中获取当前租户访问记录**/
				List<String> accessTokenList = tenantAccessTokenMap.get(tenantUuid);
				if(accessTokenList == null) {
					/** 首次访问初始化缓存 **/
					synchronized(ApiAccessCountUpdateThread.class) {
						accessTokenList = tenantAccessTokenMap.get(tenantUuid);
						if(accessTokenList == null) {
							accessTokenList = new ArrayList<>(ACCESS_COUNT_THRESHOLD);
							tenantAccessTokenMap.put(tenantUuid, accessTokenList);
						}
					}
				}
				synchronized(accessTokenList) {
					accessTokenList.add(token);
					/** 累计达到缓存次数阈值时，写入数据库并清空缓存 **/
					if(accessTokenList.size() == ACCESS_COUNT_THRESHOLD) {
						/** 统计每个接口的访问次数 **/
						Map<String, Integer> tokenAccessCountMap = new HashMap<>();
						for(String token : accessTokenList) {
							Integer count = tokenAccessCountMap.get(token);
							if(count == null) {
								tokenAccessCountMap.put(token, 1);
							}else {
								tokenAccessCountMap.put(token, count + 1);
							}
						}
						for(Entry<String, Integer> entry : tokenAccessCountMap.entrySet()) {
							String token = entry.getKey();
							TransactionStatus transactionStatus = transactionUtil.openTx();
							try {
								if(apiMapper.getApiAccessCountLockByToken(token) == null) {
									apiMapper.insertApiAccessCount(token, entry.getValue());
								}else {
									apiMapper.increaseApiAccessCount(token, entry.getValue());
								}
								transactionUtil.commitTx(transactionStatus);
							} catch (Exception e) {
								logger.error(e.getMessage(), e);
								transactionUtil.rollbackTx(transactionStatus);
							}
						}
						accessTokenList.clear();
					}
				}			
			}
		}catch(Exception e) {
			logger.error(e.getMessage(), e);
		}finally {
			Thread.currentThread().setName(oldThreadName);
		}			
	}
}
