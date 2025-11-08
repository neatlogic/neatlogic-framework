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

package neatlogic.framework.condition.core;

import neatlogic.framework.applicationlistener.core.ModuleInitializedListenerBase;
import neatlogic.framework.bootstrap.NeatLogicWebApplicationContext;
import neatlogic.framework.common.RootComponent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @ClassName: ConditionHandlerFactory
 * @Description: 条件处理器工厂类
 */
@RootComponent
public class ConditionHandlerFactory extends ModuleInitializedListenerBase {

    private static final Map<String, HashMap<String, IConditionHandler>> conditionHandlerMap = new HashMap<>();

    private static final Map<String, List<IConditionHandler>> conditionHandlerList = new HashMap<>();

    public static IConditionHandler getHandler(String belong, String name) {
        if (conditionHandlerMap.containsKey(belong)) {
            return conditionHandlerMap.get(belong).get(name);
        }
        return null;
    }

    public static IConditionHandler getHandler(String name) {
        for (Entry<String, HashMap<String, IConditionHandler>> entry : conditionHandlerMap.entrySet()) {
            if (entry.getValue().containsKey(name)) {
                return entry.getValue().get(name);
            }
        }
        return null;
    }

    public static List<IConditionHandler> getConditionHandlerList(String belong) {
        if (conditionHandlerList.containsKey(belong)) {
            return conditionHandlerList.get(belong);
        }
        return new ArrayList<>();
    }

    @Override
    public void onInitialized(NeatLogicWebApplicationContext context) {
        Map<String, IConditionHandler> myMap = context.getBeansOfType(IConditionHandler.class);
        for (Entry<String, IConditionHandler> entry : myMap.entrySet()) {
            IConditionHandler conditionHandler = entry.getValue();
            if (!conditionHandlerMap.containsKey(conditionHandler.getBelong())) {
                conditionHandlerMap.put(conditionHandler.getBelong(), new HashMap<>());
                conditionHandlerList.put(conditionHandler.getBelong(), new ArrayList<>());
            }
            conditionHandlerMap.get(conditionHandler.getBelong()).put(conditionHandler.getName(), conditionHandler);
            conditionHandlerList.get(conditionHandler.getBelong()).add(conditionHandler);
        }
    }

    @Override
    protected void myInit() {
        // TODO Auto-generated method stub

    }

}
