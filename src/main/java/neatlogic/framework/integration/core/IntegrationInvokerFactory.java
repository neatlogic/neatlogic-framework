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

package neatlogic.framework.integration.core;

import neatlogic.framework.applicationlistener.core.ModuleInitializedListenerBase;
import neatlogic.framework.bootstrap.NeatLogicWebApplicationContext;
import neatlogic.framework.common.RootComponent;

import java.util.HashMap;
import java.util.Map;

@RootComponent
public class IntegrationInvokerFactory extends ModuleInitializedListenerBase {

    private static final Map<String, IntegrationInvokerBase> componentMap = new HashMap<>();


    public static IntegrationInvokerBase getHandler(IntegrationInvokerBase handler) {
        return componentMap.get(handler.getHandler());
    }


    @Override
    protected void onInitialized(NeatLogicWebApplicationContext context) {
        Map<String, IntegrationInvokerBase> myMap = context.getBeansOfType(IntegrationInvokerBase.class);
        for (Map.Entry<String, IntegrationInvokerBase> entry : myMap.entrySet()) {
            IntegrationInvokerBase component = entry.getValue();
            if (component.getHandler() != null) {
                componentMap.put(component.getHandler(), component);
            }
        }
    }

    @Override
    protected void myInit() {

    }
}
