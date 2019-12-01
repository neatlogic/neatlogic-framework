package codedriver.framework.startup;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;

import com.alibaba.druid.pool.DruidDataSource;

import codedriver.framework.common.CodeDriverDataSource;
import codedriver.framework.common.RootComponent;
import codedriver.framework.dao.mapper.DatasourceMapper;
import codedriver.framework.dto.DatasourceVo;

@RootComponent
@Order(1)
public class DatasourceInitializer {
	@Autowired
	private DatasourceMapper datasourceMapper;

	@Autowired
	private CodeDriverDataSource datasouce;

	@Resource(name = "dataSourceMaster")
	private DruidDataSource masterDatasource;

	private static Map<Object, Object> datasourceMap = new HashMap<>();

	@PostConstruct
	public void init() {
		// TenantContext context = TenantContext.init("master");
		List<DatasourceVo> datasourceList = datasourceMapper.getAllDatasource();
		if (!datasourceMap.containsKey("master")) {
			datasourceMap.put("master", masterDatasource);
		}

		for (DatasourceVo datasourceVo : datasourceList) {
			if (!datasourceMap.containsKey(datasourceVo.getTenantUuid())) {
				DruidDataSource tenantDatasource = new DruidDataSource();
				tenantDatasource.setUrl(datasourceVo.getUrl());
				tenantDatasource.setDriverClassName(datasourceVo.getDriver());
				tenantDatasource.setUsername(datasourceVo.getUsername());
				tenantDatasource.setPassword(datasourceVo.getPassword());
				datasourceMap.put(datasourceVo.getTenantUuid(), tenantDatasource);
			}
		}
		if (!datasourceMap.isEmpty()) {
			datasouce.setTargetDataSources(datasourceMap);
			if (datasourceMap.containsKey("master")) {
				datasouce.setDefaultTargetDataSource(datasourceMap.get("master"));
			}
			datasouce.afterPropertiesSet();
		}
	}

}
