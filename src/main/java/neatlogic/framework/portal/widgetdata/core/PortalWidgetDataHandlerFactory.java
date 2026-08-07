/*
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the NeatLogic Sustainable Use License (NSUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */

package neatlogic.framework.portal.widgetdata.core;

import neatlogic.framework.applicationlistener.core.ModuleInitializedListenerBase;
import neatlogic.framework.bootstrap.NeatLogicWebApplicationContext;
import neatlogic.framework.common.RootComponent;

import java.util.HashMap;
import java.util.Map;

@RootComponent
public class PortalWidgetDataHandlerFactory extends ModuleInitializedListenerBase {

    private static final Map<String, IPortalWidgetDataHandler> map = new HashMap<>();

    public static IPortalWidgetDataHandler getHandler(String handler) {
        return map.get(handler);
    }

    /**
     * 每个模块加载完毕后执行
     *
     * @param context spring applicationContext
     */
    @Override
    protected void onInitialized(NeatLogicWebApplicationContext context) {
        Map<String, IPortalWidgetDataHandler> myMap = context.getBeansOfType(IPortalWidgetDataHandler.class);
        for (Map.Entry<String, IPortalWidgetDataHandler> entry : myMap.entrySet()) {
            IPortalWidgetDataHandler component = entry.getValue();
            if (component.getHandler() != null) {
                map.put(component.getHandler(), component);
            }
        }
    }

    /**
     * 当前类初始化完执行的操作
     **/
    @Override
    protected void myInit() {

    }
}
