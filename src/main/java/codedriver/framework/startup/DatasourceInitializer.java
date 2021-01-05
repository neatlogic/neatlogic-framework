package codedriver.framework.startup;

import codedriver.framework.common.CodeDriverBasicDataSource;
import codedriver.framework.common.CodeDriverRoutingDataSource;
import codedriver.framework.common.util.TenantUtil;
import codedriver.framework.dao.mapper.DatasourceMapper;
import codedriver.framework.dto.DatasourceVo;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @Author:chenqiwei
 * @Time:Jun 11, 2020
 * @ClassName: DatasourceInitializer
 * @Description: 此类在root-context.xml中初始化
 */
public class DatasourceInitializer {
	@Autowired
	private DatasourceMapper datasourceMapper;

	@Autowired
	private CodeDriverRoutingDataSource datasource;

	private static CodeDriverRoutingDataSource instance;

	@Resource(name = "dataSourceMaster")
	private CodeDriverBasicDataSource masterDatasource;

	private static final Map<Object, Object> datasourceMap = new HashMap<>();

	@PostConstruct
	public void init() {
		List<DatasourceVo> datasourceList = datasourceMapper.getAllActiveTenantDatasource();
		if (!datasourceMap.containsKey("master")) {
			datasourceMap.put("master", masterDatasource);
		}

		for (DatasourceVo datasourceVo : datasourceList) {
			if (!datasourceMap.containsKey(datasourceVo.getTenantUuid())) {
				// 创建OLTP库
				CodeDriverBasicDataSource tenantDatasource = new CodeDriverBasicDataSource();
				String url = datasourceVo.getUrl();
				url = url.replace("{host}", datasourceVo.getHost());
				url = url.replace("{port}", datasourceVo.getPort().toString());
				url = url.replace("{dbname}", "codedriver_" + datasourceVo.getTenantUuid());
				tenantDatasource.setUrl(url);
				tenantDatasource.setDriverClassName(datasourceVo.getDriver());
				tenantDatasource.setUsername(datasourceVo.getUsername());
				tenantDatasource.setPassword(datasourceVo.getPasswordPlain());
				datasourceMap.put(datasourceVo.getTenantUuid(), tenantDatasource);
				// 创建OLAP库
				CodeDriverBasicDataSource tenantDatasourceOlap = new CodeDriverBasicDataSource();
				String urlOlap = datasourceVo.getUrl();
				urlOlap = urlOlap.replace("{host}", datasourceVo.getHost());
				urlOlap = urlOlap.replace("{port}", datasourceVo.getPort().toString());
				urlOlap = urlOlap.replace("{dbname}", "codedriver_" + datasourceVo.getTenantUuid() + "_olap");
				tenantDatasourceOlap.setUrl(urlOlap);
				tenantDatasourceOlap.setDriverClassName(datasourceVo.getDriver());
				tenantDatasourceOlap.setUsername(datasourceVo.getUsername());
				tenantDatasourceOlap.setPassword(datasourceVo.getPasswordPlain());
				datasourceMap.put(datasourceVo.getTenantUuid() + "_OLAP", tenantDatasourceOlap);

				TenantUtil.addTenant(datasourceVo.getTenantUuid());
			}
		}
		if (instance == null && datasource != null) {
			instance = datasource;
		}
		if (!datasourceMap.isEmpty()) {
			datasource.setTargetDataSources(datasourceMap);
			if (datasourceMap.containsKey("master")) {
				datasource.setDefaultTargetDataSource(datasourceMap.get("master"));
			}
			datasource.afterPropertiesSet();
		}
	}

	public static void addDynamicDataSource(String tenantUuid, CodeDriverBasicDataSource codeDriverBasicDataSource) {
		if (!datasourceMap.containsKey(tenantUuid)) {
			datasourceMap.put(tenantUuid, codeDriverBasicDataSource);
			instance.setTargetDataSources(datasourceMap);
			if (datasourceMap.containsKey("master")) {
				instance.setDefaultTargetDataSource(datasourceMap.get("master"));
			}
			instance.afterPropertiesSet();
		}
	}

}
