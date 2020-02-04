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

	@PostConstruct
	public final void init() {
		TenantContext.init();
		TenantContext tenantContext = TenantContext.get();
		String tenant = tenantContext.getTenantUuid();
		tenantContext.switchTenant(tenant);
		myInit();
	}

	protected abstract void myInit();

}
