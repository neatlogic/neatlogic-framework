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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

@RootComponent
public class NotifyContentHandlerFactory extends ModuleInitializedListenerBase {

    private static final List<ValueTextVo> notifyContentHandlerList = new ArrayList<>();

    private static final Map<String, INotifyContentHandler> notifyContentHandlerMap = new HashMap<>();

    public static INotifyContentHandler getHandler(String handler) {
        return notifyContentHandlerMap.get(handler);
    }

    public static List<ValueTextVo> getNotifyContentHandlerList() {
        return notifyContentHandlerList;
    }

    @Override
    public void onInitialized(NeatLogicWebApplicationContext context) {
        Map<String, INotifyContentHandler> map = context.getBeansOfType(INotifyContentHandler.class);
        for (Entry<String, INotifyContentHandler> entry : map.entrySet()) {
            notifyContentHandlerMap.put(entry.getValue().getClassName(), entry.getValue());
            notifyContentHandlerList.add(new ValueTextVo(entry.getValue().getClassName(), entry.getValue().getName()));
        }
    }

    @Override
    protected void myInit() {

    }

}
