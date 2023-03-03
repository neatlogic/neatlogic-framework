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
