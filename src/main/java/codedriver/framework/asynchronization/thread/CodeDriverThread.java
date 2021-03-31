package codedriver.framework.asynchronization.thread;

import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.asynchronization.threadlocal.UserContext;
import codedriver.framework.cache.threadlocal.CacheContext;
import org.apache.commons.lang3.StringUtils;

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
        try {
            String oldThreadName = Thread.currentThread().getName();
            if (StringUtils.isNotBlank(threadName)) {
                Thread.currentThread().setName(threadName);
            }
            /* 等待所有模块加载完成 **/
            ModuleInitApplicationListener.getModuleinitphaser().awaitAdvance(0);
            execute();
            Thread.currentThread().setName(oldThreadName);
        } finally {
            // 清除所有threadlocal
            if (TenantContext.get() != null) {
                TenantContext.get().release();
            }
            if (UserContext.get() != null) {
                UserContext.get().release();
            }
            CacheContext.release();
        }
    }

    protected abstract void execute();


    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

}