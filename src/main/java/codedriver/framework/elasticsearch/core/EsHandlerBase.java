package codedriver.framework.elasticsearch.core;

import org.apache.commons.lang3.StringUtils;

import com.techsure.multiattrsearch.MultiAttrsObjectPool;

import codedriver.framework.asynchronization.threadlocal.TenantContext;

public abstract class EsHandlerBase implements IElasticSearchHandler {
	
	protected MultiAttrsObjectPool objectPool;
	
	protected MultiAttrsObjectPool getObjectPool(String poolName,String tenant) {
		if(objectPool == null) {
		    objectPool = ElasticSearchPoolManager.getObjectPool(poolName);
		}
		if(objectPool != null) {
		    if(StringUtils.isNotBlank(tenant)) {
		        objectPool.checkout(tenant);
		    }else {
		        objectPool.checkout(TenantContext.get().getTenantUuid());
		    }
		}
		return objectPool;
	}
}
