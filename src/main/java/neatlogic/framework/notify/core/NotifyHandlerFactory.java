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

package neatlogic.framework.notify.core;

import neatlogic.framework.applicationlistener.core.ModuleInitializedListenerBase;
import neatlogic.framework.bootstrap.NeatLogicWebApplicationContext;
import neatlogic.framework.common.RootComponent;
import neatlogic.framework.common.dto.ValueTextVo;
import org.springframework.util.ClassUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RootComponent
public class NotifyHandlerFactory extends ModuleInitializedListenerBase {
    private static final Map<String, INotifyHandler> notifyHandlerMap = new HashMap<>();

//    private static final List<ValueTextVo> notifyHandlerTypeList = new ArrayList<>();
//
//    private static final List<ValueTextVo> notifyHandlerNameList = new ArrayList<>();

    public static List<ValueTextVo> getNotifyHandlerTypeList() {
        List<ValueTextVo> resultList = new ArrayList<>();
        for (Map.Entry<String, INotifyHandler> entry : notifyHandlerMap.entrySet()) {
            INotifyHandler notifyHandler = entry.getValue();
            resultList.add(new ValueTextVo(ClassUtils.getUserClass(notifyHandler.getClass()).getSimpleName(), notifyHandler.getName()));
        }
        return resultList;
    }

    public static List<ValueTextVo> getNotifyHandlerNameList() {
        List<ValueTextVo> resultList = new ArrayList<>();
        for (Map.Entry<String, INotifyHandler> entry : notifyHandlerMap.entrySet()) {
            INotifyHandler notifyHandler = entry.getValue();
            resultList.add(new ValueTextVo(ClassUtils.getUserClass(notifyHandler.getClass()).getSimpleName(), notifyHandler.getType()));
        }
        return resultList;
    }

    public static INotifyHandler getHandler(String handler) {
        return notifyHandlerMap.get(handler);
    }


    @Override
    protected void onInitialized(NeatLogicWebApplicationContext context) {
        Map<String, INotifyHandler> myMap = context.getBeansOfType(INotifyHandler.class);
        for (Map.Entry<String, INotifyHandler> entry : myMap.entrySet()) {
            INotifyHandler plugin = entry.getValue();
            if (plugin.getClassName() != null) {
                notifyHandlerMap.put(ClassUtils.getUserClass(plugin.getClass()).getSimpleName(), plugin);
//                notifyHandlerTypeList.add(new ValueTextVo(plugin.getClass().getSimpleName(), plugin.getName()));
//                notifyHandlerNameList.add(new ValueTextVo(plugin.getClass().getSimpleName(), plugin.getType()));
            }
        }
    }

    @Override
    protected void myInit() {

    }
}
