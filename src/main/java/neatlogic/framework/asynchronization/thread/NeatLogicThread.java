/*Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.*/

package neatlogic.framework.asynchronization.thread;

import neatlogic.framework.asynchronization.threadlocal.*;
import neatlogic.framework.cache.threadlocal.CacheContext;
import neatlogic.framework.exception.core.ApiRuntimeException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;

public abstract class NeatLogicThread implements Runnable {
    private final static Logger logger = LoggerFactory.getLogger(NeatLogicThread.class);
    protected UserContext userContext;
    protected TenantContext tenantContext;
    protected InputFromContext inputFromContext;
    protected RequestContext requestContext;
    private String threadName;
    private boolean isUnique = false;

    private CountDownLatch countDownLatch;

    /*public NeatLogicThread() {
        userContext = UserContext.get();
        tenantContext = TenantContext.get();
        inputFromContext = InputFromContext.get();
    }*/

    public NeatLogicThread(UserContext _userContext, TenantContext _tenantContext) {
        userContext = _userContext;
        tenantContext = _tenantContext;
        inputFromContext = InputFromContext.get();
    }


    public NeatLogicThread(String _threadName) {
        userContext = UserContext.get();
        tenantContext = TenantContext.get();
        inputFromContext = InputFromContext.get();
        requestContext = RequestContext.get();
        this.threadName = _threadName;
    }

    public NeatLogicThread(String _threadName, boolean _isUnique) {
        userContext = UserContext.get();
        tenantContext = TenantContext.get();
        inputFromContext = InputFromContext.get();
        requestContext = RequestContext.get();
        this.threadName = _threadName;
        this.isUnique = _isUnique;
    }

    @Override
    public final void run() {
        TenantContext.init(tenantContext);
        UserContext.init(userContext);
        InputFromContext.init(inputFromContext);
        RequestContext.init(requestContext);
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
            if (countDownLatch != null) {
                countDownLatch.countDown();
            }
            // 清除所有threadlocal
            if (TenantContext.get() != null) {
                TenantContext.get().release();
            }
            if (UserContext.get() != null) {
                UserContext.get().release();
            }
            if (RequestContext.get() != null) {
                RequestContext.get().release();
            }
            if (InputFromContext.get() != null) {
                InputFromContext.get().release();
            }
            if (ConditionParamContext.get() != null) {
                ConditionParamContext.get().release();
            }
            if (LicensePolicyContext.get() != null) {
                LicensePolicyContext.get().release();
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

    public CountDownLatch getCountDownLatch() {
        return countDownLatch;
    }

    public void setCountDownLatch(CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
    }
}
