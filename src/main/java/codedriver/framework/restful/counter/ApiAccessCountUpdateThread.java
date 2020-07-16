package codedriver.framework.restful.counter;

import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import codedriver.framework.asynchronization.thread.CodeDriverThread;
import codedriver.framework.restful.dao.mapper.ApiMapper;

/**
 * 
* @Time:2020年7月15日
* @ClassName: ApiAccessCountUpdateThread 
* @Description: 接口访问次数统计任务类
 */
@Service
public class ApiAccessCountUpdateThread extends CodeDriverThread {
	
	private static Logger logger = LoggerFactory.getLogger(ApiAccessCountUpdateThread.class);
	
	private static ApiMapper apiMapper;
	
	@Autowired
	public void setApiMapper(ApiMapper _apiMapper) {
		apiMapper = _apiMapper;
	}
	
	private Map<String, Integer> tokenAccessCountMap;
	
	public ApiAccessCountUpdateThread(Map<String, Integer> tokenAccessCountMap) {
		this.tokenAccessCountMap = tokenAccessCountMap;
		super.setThreadName("API-ACCESS-COUNTER-UPDATE");
	}

	@Override
	protected void execute() {
		try {
			if(MapUtils.isNotEmpty(tokenAccessCountMap)) {
				for(Entry<String, Integer> entry : tokenAccessCountMap.entrySet()) {
					String token = entry.getKey();			
					if(apiMapper.getApiAccessCountLockByToken(token) == null) {
						apiMapper.insertApiAccessCount(token, entry.getValue());
					}else {
						apiMapper.increaseApiAccessCount(token, entry.getValue());
					}		
				}
			}
		}catch(Exception e) {
			logger.error(e.getMessage(), e);
		}					
	}

}
