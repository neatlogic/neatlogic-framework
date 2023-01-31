/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.framework.integration.body.core;

import org.reflections.Reflections;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ContentTypeHandlerFactory {
    private static final Map<String, IContentTypeHandler> handlerMap = new HashMap<>();

    static {
        Reflections reflections = new Reflections("neatlogic.framework.integration.body.handler");
        Set<Class<? extends IContentTypeHandler>> modules = reflections.getSubTypesOf(IContentTypeHandler.class);
        for (Class<? extends IContentTypeHandler> c : modules) {
            IContentTypeHandler handler;
            try {
                handler = c.newInstance();
                handlerMap.put(handler.getType(), handler);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }

        }
    }

    public static IContentTypeHandler getHandler(String type) {
        return handlerMap.get(type);
    }
}
