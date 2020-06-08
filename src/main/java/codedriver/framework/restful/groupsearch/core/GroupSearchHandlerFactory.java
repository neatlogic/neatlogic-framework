package codedriver.framework.restful.groupsearch.core;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import codedriver.framework.common.RootComponent;
import codedriver.framework.exception.groupsearch.GroupSearchHandlerNotFoundException;

@RootComponent
public class GroupSearchHandlerFactory implements ApplicationListener<ContextRefreshedEvent> {
	private static Map<String, IGroupSearchHandler> componentMap = new HashMap<>();

	public static IGroupSearchHandler getHandler(String handler) {
		if (!componentMap.containsKey(handler) || componentMap.get(handler) == null) {
			throw new GroupSearchHandlerNotFoundException(handler);
		}
		return componentMap.get(handler);
	}
	
	public static Map<String, IGroupSearchHandler> getComponentMap() {
		return componentMap;
	}

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		ApplicationContext context = event.getApplicationContext();
		Map<String, IGroupSearchHandler> myMap = context.getBeansOfType(IGroupSearchHandler.class);
		for (Map.Entry<String, IGroupSearchHandler> entry : myMap.entrySet()) {
			IGroupSearchHandler component = entry.getValue();
			if (component.getName() != null) {
				componentMap.put(component.getName(), component);
			}
		}
	}
}
