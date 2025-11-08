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

package neatlogic.framework.integration.authentication.core;

import org.reflections.Reflections;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class AuthenticateHandlerFactory {
    private static final Map<String, IAuthenticateHandler> handlerMap = new HashMap<>();

    static {
        Reflections reflections = new Reflections("neatlogic.framework.integration.authentication.handler");
        Set<Class<? extends IAuthenticateHandler>> modules = reflections.getSubTypesOf(IAuthenticateHandler.class);
        for (Class<? extends IAuthenticateHandler> c : modules) {
            IAuthenticateHandler handler;
            try {
                handler = c.newInstance();
                handlerMap.put(handler.getType(), handler);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }

        }
    }

    public static IAuthenticateHandler getHandler(String type) {
        return handlerMap.get(type);
    }
}
