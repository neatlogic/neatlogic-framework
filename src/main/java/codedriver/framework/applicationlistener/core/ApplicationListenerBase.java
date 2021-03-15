package codedriver.framework.applicationlistener.core;

import codedriver.framework.asynchronization.threadlocal.TenantContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import javax.annotation.PostConstruct;

public abstract class ApplicationListenerBase implements ApplicationListener<ContextRefreshedEvent> {

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
