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
        TenantContext.get().setUseMasterDatabase(true);
        myInit();
    }

    @Override
    public final void onApplicationEvent(ContextRefreshedEvent event) {
        ApplicationContext c = event.getApplicationContext();
        if (c instanceof NeatLogicWebApplicationContext) {
            TenantContext.get().setUseMasterDatabase(true);
            onInitialized((NeatLogicWebApplicationContext) c);
        }
    }

    /**
     * 每个模块加载完毕后执行
     *
     * @param context spring applicationContext
     */
    protected abstract void onInitialized(NeatLogicWebApplicationContext context);

    /**
     * 当前类初始化完执行的操作
     **/
    protected abstract void myInit();

}
