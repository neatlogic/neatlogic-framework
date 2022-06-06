/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.datawarehouse.condition;

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
        Reflections reflections = new Reflections("codedriver.framework.datawarehouse.condition.handler");
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
