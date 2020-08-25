package codedriver.framework.notify.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;

import codedriver.framework.applicationlistener.core.ApplicationListenerBase;
import codedriver.framework.common.RootComponent;
import codedriver.framework.common.dto.ValueTextVo;
@RootComponent
public class NotifyPolicyHandlerFactory extends ApplicationListenerBase{

	private static List<ValueTextVo> notifyPolicyHandlerList = new ArrayList<>();
	
	private static Map<String, INotifyPolicyHandler> notifyPolicyHandlerMap = new HashMap<>();
	
	public static INotifyPolicyHandler getHandler(String handler) {
		return notifyPolicyHandlerMap.get(handler);
	}
	
	public static List<ValueTextVo> getNotifyPolicyHandlerList(){
		return notifyPolicyHandlerList;
	}
	
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		ApplicationContext context = event.getApplicationContext();
		Map<String, INotifyPolicyHandler> map = context.getBeansOfType(INotifyPolicyHandler.class);
		for(Entry<String, INotifyPolicyHandler> entry : map.entrySet()) {
			notifyPolicyHandlerMap.put(entry.getValue().getClassName(), entry.getValue());
			notifyPolicyHandlerList.add(new ValueTextVo(entry.getValue().getClassName(), entry.getValue().getName()));
		}
	}

	@Override
	protected void myInit() {
		
	}

}
