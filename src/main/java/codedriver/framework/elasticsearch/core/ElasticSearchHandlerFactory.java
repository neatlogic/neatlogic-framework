package codedriver.framework.elasticsearch.core;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;

import codedriver.framework.applicationlistener.core.ApplicationListenerBase;
import codedriver.framework.common.RootComponent;

@RootComponent
public class ElasticSearchHandlerFactory extends ApplicationListenerBase {
	private static Map<String, IElasticSearchHandler> handlerMap = new HashMap<String, IElasticSearchHandler>();

	public static IElasticSearchHandler getHandler(String handler) {
		return handlerMap.get(handler);
	}

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		ApplicationContext context = event.getApplicationContext();
		Map<String, IElasticSearchHandler> myMap = context.getBeansOfType(IElasticSearchHandler.class);
		for (Map.Entry<String, IElasticSearchHandler> entry : myMap.entrySet()) {
			try {
				IElasticSearchHandler handler = entry.getValue();
				handlerMap.put(handler.getDocument(), handler);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	protected void myInit() {
		// TODO Auto-generated method stub

	}
}
