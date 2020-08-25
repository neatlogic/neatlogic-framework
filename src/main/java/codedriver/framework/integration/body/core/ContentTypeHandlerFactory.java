package codedriver.framework.integration.body.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;

public class ContentTypeHandlerFactory {
	private static Map<String, IContentTypeHandler> handlerMap = new HashMap<>();
	static {
		Reflections reflections = new Reflections("codedriver.framework.integration.authtication.contenttype.handler");
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
