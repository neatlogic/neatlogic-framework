/*
 *
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the NeatLogic Sustainable Use License (NSUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */

package neatlogic.framework.listener;

import neatlogic.framework.asynchronization.threadlocal.*;
import neatlogic.framework.cache.threadlocal.CacheContext;
import neatlogic.framework.dao.plugin.SqlCostInterceptor;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;

public class ThreadlocalClearListener implements ServletRequestListener {
    @Override
    public void requestDestroyed(ServletRequestEvent event) {
//        // URL SQL监控需要在请求销毁前统一写入请求级审计列表
//        SqlCostInterceptor.completeRequestSqlAudit();
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
        if (MongodbSessionContext.get() != null) {
            MongodbSessionContext.get().release();
        }
        CacheContext.release();
    }

    @Override
    public void requestInitialized(ServletRequestEvent event) {
        //必须实现方法 否则遇到低版本的javaee-api 会报错
    }
}
