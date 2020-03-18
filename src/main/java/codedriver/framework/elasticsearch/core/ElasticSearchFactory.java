package codedriver.framework.elasticsearch.core;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import codedriver.framework.exception.elasticsearch.ElatsticSearchHandlerNotFoundException;

public class ElasticSearchFactory implements ApplicationListener<ContextRefreshedEvent> {
	private static Map<String, IElasticSearchHandler> handlerMap = new HashMap<String, IElasticSearchHandler>();
	
	public static IElasticSearchHandler getAuthInstance(String handler) {
		return handlerMap.get(handler);
	}

	public static IElasticSearchHandler getHandler(String handler) {
		if (!handlerMap.containsKey(handler) || handlerMap.get(handler) == null) {
			throw new ElatsticSearchHandlerNotFoundException(handler);
		}
		return handlerMap.get(handler);
	}
	
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		ApplicationContext context = event.getApplicationContext();
		Map<String, IElasticSearchHandler> myMap = context.getBeansOfType(IElasticSearchHandler.class);
		for (Map.Entry<String, IElasticSearchHandler> entry : myMap.entrySet()) {
			try {
				IElasticSearchHandler handler = entry.getValue();
				handlerMap.put(handler.getHandler(), handler);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
}
