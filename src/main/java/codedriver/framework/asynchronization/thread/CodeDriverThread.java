package codedriver.framework.asynchronization.thread;

import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.asynchronization.threadlocal.UserContext;

public abstract class CodeDriverThread implements Runnable {

	protected UserContext userContext;
	protected TenantContext tenantContext;
	
	public CodeDriverThread() {
		userContext = UserContext.get();
		tenantContext = TenantContext.get();
	}

	@Override
	public final void run() {
		userContext = UserContext.get();
		tenantContext = TenantContext.get();
		execute();
	}

	protected abstract void execute();

}
