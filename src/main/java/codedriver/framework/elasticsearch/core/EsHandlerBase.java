package codedriver.framework.elasticsearch.core;

import org.apache.commons.lang3.StringUtils;

import com.techsure.multiattrsearch.MultiAttrsObjectPool;

public abstract class EsHandlerBase implements IElasticSearchHandler {
	
	protected MultiAttrsObjectPool objectPool;
	
	protected MultiAttrsObjectPool getObjectPool(String poolName,String tenant) {
		if(objectPool == null) {
		    objectPool = ElasticSearchPoolManager.getObjectPool(poolName);
		}
		if(objectPool != null && StringUtils.isNotBlank(tenant)) {
		    objectPool.checkout(tenant);

		}
		return objectPool;
	}
}
