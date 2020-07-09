package codedriver.framework.condition.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;

import codedriver.framework.applicationlistener.core.ApplicationListenerBase;
import codedriver.framework.common.RootComponent;

@RootComponent
public class ConditionHandlerFactory extends ApplicationListenerBase {

	private static Map<String, IConditionHandler> conditionHandlerMap = new HashMap<>();
	
	public static IConditionHandler getHandler(String name) {
		return conditionHandlerMap.get(name);
	}
	
	public static List<IConditionHandler> getConditionHandlerList(){
		List<IConditionHandler> conditionHandlerList = new ArrayList<>();
		for(Entry<String, IConditionHandler> entry : conditionHandlerMap.entrySet()) {
			conditionHandlerList.add(entry.getValue());
		}
		return conditionHandlerList;
	}
	
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		ApplicationContext context = event.getApplicationContext();
		Map<String, IConditionHandler> myMap = context.getBeansOfType(IConditionHandler.class);
		for(Entry<String, IConditionHandler> entry : myMap.entrySet()) {
			conditionHandlerMap.put(entry.getValue().getName(), entry.getValue());
		}
	}

	@Override
	protected void myInit() {
		// TODO Auto-generated method stub
		
	}

}
