package codedriver.framework.applicationlistener.core;

import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.common.util.ModuleUtil;
import codedriver.framework.dao.mapper.ModuleMapper;
import codedriver.framework.dao.mapper.TenantMapper;

public abstract class ApplicationListenerBase implements ApplicationListener<ContextRefreshedEvent> {
	@Autowired
	protected TenantMapper tenantMapper;
	@Autowired
	protected ModuleMapper moduleMapper;

	@PostConstruct
	public final void init() {
		TenantContext.init();
		TenantContext tenantContext = TenantContext.get();
		String tenant = tenantContext.getTenantUuid();
		if (StringUtils.isNotBlank(tenant)) {
			// 使用master库
			tenantContext.setUseDefaultDatasource(true);
			List<String> tenentModuleList = moduleMapper.getModuleListByTenantUuid(tenant);
			// 还原回租户库
			tenantContext.setUseDefaultDatasource(false);
			// 设置当前租户激活模块信息
			tenantContext.setActiveModuleList(ModuleUtil.getTenantActionModuleList(tenentModuleList));
		}
		myInit();
	}

	protected abstract void myInit();

}
