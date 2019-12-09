package codedriver.framework.attribute.core;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import codedriver.framework.attribute.dao.mapper.AttributeMapper;
import codedriver.framework.common.RootComponent;

@RootComponent
public class AttributeHandlerFactory implements ApplicationListener<ContextRefreshedEvent> {
	private static Map<String, IAttributeHandler> handlerMap = new HashMap<>();

	@Autowired
	private AttributeMapper attributeMapper;

	public static IAttributeHandler getHandler(String type) {
		return handlerMap.get(type);
	}

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		ApplicationContext context = event.getApplicationContext();
		Map<String, IAttributeHandler> myMap = context.getBeansOfType(IAttributeHandler.class);
		for (Map.Entry<String, IAttributeHandler> entry : myMap.entrySet()) {
			IAttributeHandler handler = entry.getValue();
			if (handler.getType() != null) {
				handlerMap.put(handler.getType(), handler);
			}
		}
	}
}
