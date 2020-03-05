package codedriver.framework.startup;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;

import codedriver.framework.common.CodeDriverBasicDataSource;
import codedriver.framework.common.CodeDriverRoutingDataSource;
import codedriver.framework.common.util.TenantUtil;
import codedriver.framework.dao.mapper.DatasourceMapper;
import codedriver.framework.dto.DatasourceVo;

public class DatasourceInitializer {
	@Autowired
	private DatasourceMapper datasourceMapper;

	@Autowired
	private CodeDriverRoutingDataSource datasource;

	private static CodeDriverRoutingDataSource instance;

	@Resource(name = "dataSourceMaster")
	private CodeDriverBasicDataSource masterDatasource;

	private static Map<Object, Object> datasourceMap = new HashMap<>();

	@PostConstruct
	public void init() {
		// TenantContext context = TenantContext.init("master");
		List<DatasourceVo> datasourceList = datasourceMapper.getAllActiveTenantDatasource();
		if (!datasourceMap.containsKey("master")) {
			datasourceMap.put("master", masterDatasource);
		}

		for (DatasourceVo datasourceVo : datasourceList) {
			if (!datasourceMap.containsKey(datasourceVo.getTenantUuid())) {
				CodeDriverBasicDataSource tenantDatasource = new CodeDriverBasicDataSource();
				tenantDatasource.setUrl(datasourceVo.getUrl());
				tenantDatasource.setDriverClassName(datasourceVo.getDriver());
				tenantDatasource.setUsername(datasourceVo.getUsername());
				tenantDatasource.setPassword(datasourceVo.getPassword());
				datasourceMap.put(datasourceVo.getTenantUuid(), tenantDatasource);
				TenantUtil.addTenant(datasourceVo.getTenantUuid());
			}
		}
		if (instance == null && datasource != null){
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

	public static void addDynamicDataSource(String tenantUuid, CodeDriverBasicDataSource codeDriverBasicDataSource){
	    datasourceMap.put(tenantUuid, codeDriverBasicDataSource);
        instance.setTargetDataSources(datasourceMap);
        if (datasourceMap.containsKey("master")) {
            instance.setDefaultTargetDataSource(datasourceMap.get("master"));
        }
        instance.afterPropertiesSet();
    }

}
