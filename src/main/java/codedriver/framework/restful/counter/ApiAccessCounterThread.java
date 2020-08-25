package codedriver.framework.restful.counter;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import codedriver.framework.asynchronization.thread.CodeDriverThread;
import codedriver.framework.asynchronization.threadlocal.TenantContext;

/**
 * 
* @Time:2020年7月15日
* @ClassName: ApiAccessCounterThread 
* @Description: 模拟接口访问任务类
 */
public class ApiAccessCounterThread extends CodeDriverThread {
	
	private static Logger logger = LoggerFactory.getLogger(ApiAccessCounterThread.class);
	
	private String tenantUuid;
	
	private String token;
	
	public ApiAccessCounterThread(int i, String tenantUuid, String token) {
		this.tenantUuid = tenantUuid;
		this.token = token;
		super.setThreadName("API-ACCESS-COUNTER" + i);
	}

	@Override
	protected void execute() {
		try {
			if(StringUtils.isNotBlank(tenantUuid) && StringUtils.isNotBlank(token) ) {
				TenantContext.init(tenantUuid);
				ApiAccessCountUpdateThread.putToken(token);
			}
		}catch(Exception e) {
			logger.error(e.getMessage(), e);
		}					
	}

}
