/*
 * Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

public abstract class NeatLogicThread implements Runnable {
    private final static Logger logger = LoggerFactory.getLogger(NeatLogicThread.class);
    protected UserContext userContext;
    protected TenantContext tenantContext;
    protected InputFromContext inputFromContext;
    private String threadName;
    private boolean isUnique = false;

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
        this.threadName = _threadName;
    }

    public NeatLogicThread(String _threadName, boolean _isUnique) {
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