package codedriver.framework.integration.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import codedriver.framework.common.RootComponent;
import codedriver.framework.integration.dto.IntegrationHandlerVo;

@RootComponent
public class IntegrationHandlerFactory implements ApplicationListener<ContextRefreshedEvent> {

	private static Map<String, IIntegrationHandler<?>> componentMap = new HashMap<>();
	private static List<IntegrationHandlerVo> handlerList = new ArrayList<>();

	public static IIntegrationHandler<?> getHandler(String handler) {
		return componentMap.get(handler);
	}

	public static IIntegrationHandler<?> getHandler(IIntegrationHandler handler) {
		return componentMap.get(handler.getHandler());
	}

	public static List<IntegrationHandlerVo> getHandlerList() {
		return handlerList;
	}

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		ApplicationContext context = event.getApplicationContext();
		Map<String, IIntegrationHandler> myMap = context.getBeansOfType(IIntegrationHandler.class);
		for (Map.Entry<String, IIntegrationHandler> entry : myMap.entrySet()) {
			IIntegrationHandler component = entry.getValue();
			if (component.getHandler() != null) {
				componentMap.put(component.getHandler(), component);
				handlerList.add(new IntegrationHandlerVo(component.getName(), component.getHandler()));
			}
		}

	}
}
