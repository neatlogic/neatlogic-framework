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

package neatlogic.framework.mq.core;

import neatlogic.framework.applicationlistener.core.ModuleInitializedListenerBase;
import neatlogic.framework.bootstrap.NeatLogicWebApplicationContext;
import neatlogic.framework.common.RootComponent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RootComponent
public class MqHandlerFactory extends ModuleInitializedListenerBase {
    private static final Map<String, IMqHandler> componentMap = new HashMap<>();
    private static final List<IMqHandler> handlerList = new ArrayList<>();

    @Override
    protected void onInitialized(NeatLogicWebApplicationContext context) {
        Map<String, IMqHandler> myMap = context.getBeansOfType(IMqHandler.class);
        for (Map.Entry<String, IMqHandler> entry : myMap.entrySet()) {
            IMqHandler component = entry.getValue();
            componentMap.put(component.getName(), component);
            handlerList.add(component);
        }
    }

    public static IMqHandler getMqHandler(String componentName) {
        return componentMap.get(componentName);
    }

    public static List<IMqHandler> getMqHandlerList() {
        return handlerList;
    }

    @Override
    protected void myInit() {

    }
}
