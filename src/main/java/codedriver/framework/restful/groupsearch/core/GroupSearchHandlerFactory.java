package codedriver.framework.restful.groupsearch.core;

import codedriver.framework.applicationlistener.core.ModuleInitializedListenerBase;
import codedriver.framework.bootstrap.CodedriverWebApplicationContext;
import codedriver.framework.common.RootComponent;
import codedriver.framework.exception.groupsearch.GroupSearchHandlerNotFoundException;

import java.util.HashMap;
import java.util.Map;

@RootComponent
public class GroupSearchHandlerFactory extends ModuleInitializedListenerBase {
	private static final Map<String, IGroupSearchHandler> componentMap = new HashMap<>();

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
	protected void onInitialized(CodedriverWebApplicationContext context) {
		Map<String, IGroupSearchHandler> myMap = context.getBeansOfType(IGroupSearchHandler.class);
		for (Map.Entry<String, IGroupSearchHandler> entry : myMap.entrySet()) {
			IGroupSearchHandler component = entry.getValue();
			if (component.getName() != null) {
				componentMap.put(component.getName(), component);
			}
		}
	}

	@Override
	protected void myInit() {

	}
}
