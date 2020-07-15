package codedriver.framework.restful.audit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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

@Service
public class ApiAccessCountUpdateThread extends CodeDriverThread {
	
	private static  Logger logger = LoggerFactory.getLogger(ApiAccessCountUpdateThread.class);
	
	private final static int ACCESS_COUNT_THRESHOLD = 1000;
	
	private static ApiMapper apiMapper;

	private static TransactionUtil transactionUtil;

	private static Map<String, List<String>> tenantAccessTokenMap = new HashMap<>();
	
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
		if(StringUtils.isNotBlank(token)) {
			String tenantUuid = TenantContext.get().getTenantUuid();
			synchronized(ApiAccessCountUpdateThread.class) {
				List<String> accessTokenList = tenantAccessTokenMap.get(tenantUuid);
				if(accessTokenList == null) {
					accessTokenList = new ArrayList<>(ACCESS_COUNT_THRESHOLD);
					tenantAccessTokenMap.put(tenantUuid, accessTokenList);
				}
				accessTokenList.add(token);
				System.out.println(accessTokenList.size());
				if(accessTokenList.size() == ACCESS_COUNT_THRESHOLD) {
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
	}
}
