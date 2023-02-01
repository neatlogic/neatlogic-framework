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
