/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.integration.authtication.core;

import org.reflections.Reflections;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class AuthenticateHandlerFactory {
	private static final Map<String, IAuthenticateHandler> handlerMap = new HashMap<>();
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
