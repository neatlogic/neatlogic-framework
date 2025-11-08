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

package neatlogic.framework.applicationlistener.core;

import neatlogic.framework.bootstrap.NeatLogicWebApplicationContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import javax.annotation.PostConstruct;

public abstract class ModuleInitializedListenerBase implements ApplicationListener<ContextRefreshedEvent> {

    @PostConstruct
    public final void init() {
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
