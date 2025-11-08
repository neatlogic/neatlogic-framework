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

package neatlogic.framework.restful.groupsearch.core;

import neatlogic.framework.applicationlistener.core.ModuleInitializedListenerBase;
import neatlogic.framework.bootstrap.NeatLogicWebApplicationContext;
import neatlogic.framework.common.RootComponent;
import neatlogic.framework.exception.groupsearch.GroupSearchHandlerNotFoundException;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

@RootComponent
public class GroupSearchHandlerFactory extends ModuleInitializedListenerBase {
    private static final Map<String, IGroupSearchHandler> componentMap = new HashMap<>();

    public static IGroupSearchHandler getHandler(String handler) {
        if (!componentMap.containsKey(handler) || componentMap.get(handler) == null) {
            throw new GroupSearchHandlerNotFoundException(handler);
        }
        return componentMap.get(handler);
    }

    public static Map<String, IGroupSearchHandler> getComponentMap() {
        return componentMap;
    }


    @Override
    protected void onInitialized(NeatLogicWebApplicationContext context) {
        Map<String, IGroupSearchHandler> myMap = context.getBeansOfType(IGroupSearchHandler.class);
        for (Map.Entry<String, IGroupSearchHandler> entry : myMap.entrySet()) {
            IGroupSearchHandler component = entry.getValue();
            if (StringUtils.isNotBlank(component.getName())) {
                componentMap.put(component.getName(), component);
            }
        }
    }

    @Override
    protected void myInit() {

    }
}
