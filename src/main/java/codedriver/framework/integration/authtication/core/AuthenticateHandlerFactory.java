package codedriver.framework.integration.authtication.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;

public class AuthenticateHandlerFactory {
	private static Map<String, IAuthenticateHandler> handlerMap = new HashMap<>();
	static {
		Reflections reflections = new Reflections("codedriver.framework.integration.authtication.handler");
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
