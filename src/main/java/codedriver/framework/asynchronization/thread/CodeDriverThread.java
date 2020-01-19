package codedriver.framework.asynchronization.thread;

import org.apache.commons.lang3.StringUtils;

import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.asynchronization.threadlocal.UserContext;

public abstract class CodeDriverThread implements Runnable {

	protected UserContext userContext;
	protected TenantContext tenantContext;
	private String threadName;

	public CodeDriverThread() {
		userContext = UserContext.get();
		tenantContext = TenantContext.get();
	}

	@Override
	public final void run() {
		TenantContext.init(tenantContext);
		UserContext.init(userContext);
		if (StringUtils.isNotBlank(threadName)) {
			Thread.currentThread().setName(threadName);
		}
		execute();

	}

	protected abstract void execute();

	public String getThreadName() {
		return threadName;
	}

	public void setThreadName(String threadName) {
		this.threadName = threadName;
	}

}