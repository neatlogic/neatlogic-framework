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
public class IntegrationInvokerFactory implements ApplicationListener<ContextRefreshedEvent> {

	private static Map<String, IntegrationInvokerBase> componentMap = new HashMap<>();


	public static IntegrationInvokerBase getHandler(IntegrationInvokerBase handler) {
		return componentMap.get(handler.getHandler());
	}

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		ApplicationContext context = event.getApplicationContext();
		Map<String, IntegrationInvokerBase> myMap = context.getBeansOfType(IntegrationInvokerBase.class);
		for (Map.Entry<String, IntegrationInvokerBase> entry : myMap.entrySet()) {
			IntegrationInvokerBase component = entry.getValue();
			if (component.getHandler() != null) {
				componentMap.put(component.getHandler(), component);
			}
		}

	}
}
