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

package neatlogic.framework.datawarehouse.condition;

import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DatasourceConditionHandlerFactory {
    private static final Logger logger = LoggerFactory.getLogger(DatasourceConditionHandlerFactory.class);
    private static final Map<String, IDatasourceConditionHandler> handlerMap = new HashMap<>();

    static {
        Reflections reflections = new Reflections("neatlogic.framework.datawarehouse.condition.handler");
        Set<Class<? extends IDatasourceConditionHandler>> modules = reflections.getSubTypesOf(IDatasourceConditionHandler.class);
        for (Class<? extends IDatasourceConditionHandler> c : modules) {
            IDatasourceConditionHandler handler;
            try {
                handler = c.newInstance();
                handlerMap.put(handler.getName(), handler);
            } catch (InstantiationException | IllegalAccessException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    public static IDatasourceConditionHandler getHandler(String type) {
        return handlerMap.get(type);
    }

}
