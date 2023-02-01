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
