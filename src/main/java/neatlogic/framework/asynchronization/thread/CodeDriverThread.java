/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.framework.asynchronization.thread;

import neatlogic.framework.asynchronization.threadlocal.InputFromContext;
import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.asynchronization.threadlocal.UserContext;
import neatlogic.framework.cache.threadlocal.CacheContext;
import neatlogic.framework.exception.core.ApiRuntimeException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class CodeDriverThread implements Runnable {
    private final static Logger logger = LoggerFactory.getLogger(CodeDriverThread.class);
    protected UserContext userContext;
    protected TenantContext tenantContext;
    protected InputFromContext inputFromContext;
    private String threadName;
    private boolean isUnique = false;

    /*public CodeDriverThread() {
        userContext = UserContext.get();
        tenantContext = TenantContext.get();
        inputFromContext = InputFromContext.get();
    }*/

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

    public CodeDriverThread(String _threadName, boolean _isUnique) {
        userContext = UserContext.get();
        tenantContext = TenantContext.get();
        inputFromContext = InputFromContext.get();
        this.threadName = _threadName;
        this.isUnique = _isUnique;
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
            /* 等待所有模块加载完成后，phaser将会变成1，线程才开始执行 **/
            ModuleInitApplicationListener.getModuleinitphaser().awaitAdvance(0);
            execute();
            Thread.currentThread().setName(oldThreadName);
        } catch (ApiRuntimeException ex) {
            logger.warn(ex.getMessage(), ex);
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

    public void setUnique(boolean unique) {
        isUnique = unique;
    }

    public boolean isUnique() {
        return isUnique;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    public String getThreadName() {
        return threadName;
    }
}