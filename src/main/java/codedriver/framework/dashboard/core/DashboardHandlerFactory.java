package codedriver.framework.dashboard.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import codedriver.framework.common.RootComponent;
import codedriver.framework.dashboard.dto.DashboardHandlerVo;
import codedriver.framework.exception.dashboard.DashboardHandlerNotFoundException;

@RootComponent
public class DashboardHandlerFactory implements ApplicationListener<ContextRefreshedEvent> {
	private static Map<String, IDashboardHandler> componentMap = new HashMap<>();
	private static List<DashboardHandlerVo> dashboardHandlerList = new ArrayList<>();

	public static IDashboardHandler getHandler(String handler) {
		if (!componentMap.containsKey(handler) || componentMap.get(handler) == null) {
			throw new DashboardHandlerNotFoundException(handler);
		}
		return componentMap.get(handler);
	}

	public static List<DashboardHandlerVo> getDashboardHandlerList() {
		return dashboardHandlerList;
	}

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		ApplicationContext context = event.getApplicationContext();
		Map<String, IDashboardHandler> myMap = context.getBeansOfType(IDashboardHandler.class);
		for (Map.Entry<String, IDashboardHandler> entry : myMap.entrySet()) {
			IDashboardHandler component = entry.getValue();
			if (component.getClassName() != null) {
				componentMap.put(component.getClassName(), component);

				DashboardHandlerVo dashboardHandlerVo = new DashboardHandlerVo();
				dashboardHandlerVo.setHandler(component.getClassName());
				dashboardHandlerVo.setName(component.getName());
				dashboardHandlerVo.setDisplayName(component.getDisplayName());
				dashboardHandlerVo.setType(component.getType());
				dashboardHandlerVo.setModuleId(context.getId());
				dashboardHandlerVo.setIcon(component.getIcon());
				dashboardHandlerList.add(dashboardHandlerVo);
			}
		}
	}
}
