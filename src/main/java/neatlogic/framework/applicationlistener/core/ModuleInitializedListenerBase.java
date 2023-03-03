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

package neatlogic.framework.applicationlistener.core;

import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.bootstrap.NeatLogicWebApplicationContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import javax.annotation.PostConstruct;

public abstract class ModuleInitializedListenerBase implements ApplicationListener<ContextRefreshedEvent> {

    @PostConstruct
    public final void init() {
        TenantContext.init();
        TenantContext tenantContext = TenantContext.get();
        String tenant = tenantContext.getTenantUuid();
        tenantContext.switchTenant(tenant);
        myInit();
    }

    @Override
    public final void onApplicationEvent(ContextRefreshedEvent event) {
        ApplicationContext c = event.getApplicationContext();
        if (c instanceof NeatLogicWebApplicationContext) {
            onInitialized((NeatLogicWebApplicationContext) c);
        }
    }

    /**
     * 整个应用启动完后执行的操作
     *
     * @param context spring applicationContext
     */
    protected abstract void onInitialized(NeatLogicWebApplicationContext context);

    /**
     * 当前类初始化完执行的操作
     **/
    protected abstract void myInit();

}
