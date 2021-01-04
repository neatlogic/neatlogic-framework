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

    public CodeDriverThread(String _threadName) {
        userContext = UserContext.get();
        tenantContext = TenantContext.get();
        this.threadName = _threadName;
    }

    @Override
    public final void run() {
        TenantContext.init(tenantContext);
        UserContext.init(userContext);
        String oldThreadName = Thread.currentThread().getName();
        if (StringUtils.isNotBlank(threadName)) {
            Thread.currentThread().setName(threadName);
        }
        /** 等待所有模块加载完成 **/
        ModuleInitApplicationListener.getModuleinitphaser().awaitAdvance(0);
        execute();
        Thread.currentThread().setName(oldThreadName);
    }

    protected abstract void execute();

    public String getThreadName() {
        return threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

}