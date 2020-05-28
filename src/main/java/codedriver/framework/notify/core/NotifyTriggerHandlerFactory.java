package codedriver.framework.notify.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;

import codedriver.framework.applicationlistener.core.ApplicationListenerBase;
import codedriver.framework.common.RootComponent;
@RootComponent
public class NotifyTriggerHandlerFactory extends ApplicationListenerBase{

	private static Map<String, INotifyTriggerHandler> notifyTriggerHandlerMap = new HashMap<>();
	
	public static INotifyTriggerHandler getHandler(String moduleId) {
		return notifyTriggerHandlerMap.get(moduleId);
	}
	
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		ApplicationContext context = event.getApplicationContext();
		Map<String, INotifyTriggerHandler> map = context.getBeansOfType(INotifyTriggerHandler.class);
		for(Entry<String, INotifyTriggerHandler> entry : map.entrySet()) {
			notifyTriggerHandlerMap.put(context.getId(), entry.getValue());
		}
	}

	@Override
	protected void myInit() {
		
	}

}
