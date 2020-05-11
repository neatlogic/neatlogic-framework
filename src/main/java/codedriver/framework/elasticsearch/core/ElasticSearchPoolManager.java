package codedriver.framework.elasticsearch.core;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techsure.multiattrsearch.MultiAttrsObjectPool;
import com.techsure.multiattrsearch.MultiAttrsSearch;
import com.techsure.multiattrsearch.MultiAttrsSearchConfig;

import codedriver.framework.common.config.Config;

public class ElasticSearchPoolManager {
	static Logger logger = LoggerFactory.getLogger(ElasticSearchPoolManager.class);
	private static MultiAttrsObjectPool workcenterObjectPool;
	
	public static MultiAttrsObjectPool getObjectPool(String poolName){
		if(workcenterObjectPool == null) {
			if (!Config.ES_ENABLE()) {
	            return null;
	        }
			Map<String, String> esClusters = Config.ES_CLUSTERS();
			if (esClusters.isEmpty()) {
				throw new IllegalStateException("ES集群信息未配置，es.cluster.<cluster-name>=<ip:port>[,<ip:port>...]");
			}
	
			MultiAttrsSearchConfig config = new MultiAttrsSearchConfig();
			config.setPoolName(poolName);
	
			Map.Entry<String, String> cluster = esClusters.entrySet().iterator().next();
			config.addCluster(cluster.getKey(), cluster.getValue());
			if (esClusters.size() > 1) {
				logger.warn("multiple clusters available, only cluster {} was used (picked randomly) for testing", cluster.getKey());
			}
			workcenterObjectPool = MultiAttrsSearch.getObjectPool(config);
		}
		
		return workcenterObjectPool;
	}
}
