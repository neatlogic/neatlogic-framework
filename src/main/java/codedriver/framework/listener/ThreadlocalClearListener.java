/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.listener;

import codedriver.framework.asynchronization.threadlocal.RequestContext;
import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.asynchronization.threadlocal.UserContext;
import codedriver.framework.cache.threadlocal.CacheContext;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;

public class ThreadlocalClearListener implements ServletRequestListener {
    @Override
    public void requestDestroyed(ServletRequestEvent event) {
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
        CacheContext.release();
    }
}
