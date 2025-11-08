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

package neatlogic.framework.form.attribute.core;

import neatlogic.framework.applicationlistener.core.ModuleInitializedListenerBase;
import neatlogic.framework.bootstrap.NeatLogicWebApplicationContext;
import neatlogic.framework.common.RootComponent;

import java.util.*;

@RootComponent
public class FormAttributeHandlerFactory extends ModuleInitializedListenerBase {
    private static final Map<String, IFormAttributeHandler> handlerMap = new HashMap<>();
    private static final List<IFormAttributeHandler> handlerList = new ArrayList<>();

    public static IFormAttributeHandler getHandler(String type) {
        return handlerMap.get(type);
    }

    public static List<IFormAttributeHandler> getHandlerList() {
        handlerList.sort(Comparator.comparingInt(IFormAttributeHandler::getSort));
        return handlerList;
    }

    @Override
    public void onInitialized(NeatLogicWebApplicationContext context) {
        Map<String, IFormAttributeHandler> myMap = context.getBeansOfType(IFormAttributeHandler.class);
        for (Map.Entry<String, IFormAttributeHandler> entry : myMap.entrySet()) {
            IFormAttributeHandler handler = entry.getValue();
            if (handler.getHandler() != null) {
                if (handlerMap.containsKey(handler.getHandler())) {
                    System.err.println("表单插件：" + handler.getHandler() + "已存在，请检查代码");
                } else {
                    handlerMap.put(handler.getHandler(), handler);
                    handlerList.add(handler);
                }
            }
        }
    }

    @Override
    protected void myInit() {

    }
}
