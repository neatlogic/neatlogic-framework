/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.asynchronization.thread;

import codedriver.framework.asynchronization.threadlocal.InputFromContext;
import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.asynchronization.threadlocal.UserContext;
import codedriver.framework.cache.threadlocal.CacheContext;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class CodeDriverThread implements Runnable {
    private final static Logger logger = LoggerFactory.getLogger(CodeDriverThread.class);
    protected UserContext userContext;
    protected TenantContext tenantContext;
    protected InputFromContext inputFromContext;
    private String threadName;

    public CodeDriverThread() {
        userContext = UserContext.get();
        tenantContext = TenantContext.get();
        inputFromContext = InputFromContext.get();
    }

    public CodeDriverThread(UserContext _userContext, TenantContext _tenantContext) {
        userContext = _userContext;
        tenantContext = _tenantContext;
        inputFromContext = InputFromContext.get();
    }


    public CodeDriverThread(String _threadName) {
        userContext = UserContext.get();
        tenantContext = TenantContext.get();
        inputFromContext = InputFromContext.get();
        this.threadName = _threadName;
    }

    @Override
    public final void run() {
        TenantContext.init(tenantContext);
        UserContext.init(userContext);
        InputFromContext.init(inputFromContext);
        try {
            String oldThreadName = Thread.currentThread().getName();
            if (StringUtils.isNotBlank(threadName)) {
                Thread.currentThread().setName(threadName);
            }
            /* 等待所有模块加载完成 **/
            ModuleInitApplicationListener.getModuleinitphaser().awaitAdvance(0);
            execute();
            Thread.currentThread().setName(oldThreadName);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        } finally {
            // 清除所有threadlocal
            if (TenantContext.get() != null) {
                TenantContext.get().release();
            }
            if (UserContext.get() != null) {
                UserContext.get().release();
            }
            if (InputFromContext.get() != null) {
                InputFromContext.get().release();
            }
            CacheContext.release();
        }
    }

    protected abstract void execute();


    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

}