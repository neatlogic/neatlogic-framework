package codedriver.framework.integration.core;

import codedriver.framework.applicationlistener.core.ModuleInitializedListenerBase;
import codedriver.framework.bootstrap.CodedriverWebApplicationContext;
import codedriver.framework.common.RootComponent;

import java.util.HashMap;
import java.util.Map;

@RootComponent
public class IntegrationInvokerFactory extends ModuleInitializedListenerBase {

	private static Map<String, IntegrationInvokerBase> componentMap = new HashMap<>();


	public static IntegrationInvokerBase getHandler(IntegrationInvokerBase handler) {
		return componentMap.get(handler.getHandler());
	}

	@Override
	protected void onInitialized(CodedriverWebApplicationContext context) {
		Map<String, IntegrationInvokerBase> myMap = context.getBeansOfType(IntegrationInvokerBase.class);
		for (Map.Entry<String, IntegrationInvokerBase> entry : myMap.entrySet()) {
			IntegrationInvokerBase component = entry.getValue();
			if (component.getHandler() != null) {
				componentMap.put(component.getHandler(), component);
			}
		}
	}

	@Override
	protected void myInit() {

	}
}
