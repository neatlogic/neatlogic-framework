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
import neatlogic.framework.integration.dto.IntegrationHandlerVo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RootComponent
public class IntegrationHandlerFactory extends ModuleInitializedListenerBase {

    private static final Map<String, IIntegrationHandler> componentMap = new HashMap<>();
    private static final List<IntegrationHandlerVo> handlerList = new ArrayList<>();

    public static IIntegrationHandler getHandler(String handler) {
        return componentMap.get(handler);
    }

    public static IIntegrationHandler getHandler(IIntegrationHandler handler) {
        return componentMap.get(handler.getHandler());
    }

    public static List<IntegrationHandlerVo> getHandlerList() {
        return handlerList;
    }


    @Override
    protected void onInitialized(NeatLogicWebApplicationContext context) {
        Map<String, IIntegrationHandler> myMap = context.getBeansOfType(IIntegrationHandler.class);
        for (Map.Entry<String, IIntegrationHandler> entry : myMap.entrySet()) {
            IIntegrationHandler component = entry.getValue();
            if (component.getHandler() != null) {
                componentMap.put(component.getHandler(), component);
                handlerList.add(new IntegrationHandlerVo(component.getName(), component.getHandler()));
            }
        }
    }

    @Override
    protected void myInit() {

    }
}
