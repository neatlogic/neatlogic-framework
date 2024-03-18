/*Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.*/

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
