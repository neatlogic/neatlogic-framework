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
