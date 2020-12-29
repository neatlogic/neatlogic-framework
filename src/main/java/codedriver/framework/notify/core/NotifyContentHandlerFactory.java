package codedriver.framework.notify.core;

import codedriver.framework.applicationlistener.core.ApplicationListenerBase;
import codedriver.framework.common.RootComponent;
import codedriver.framework.common.dto.ValueTextVo;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

@RootComponent
public class NotifyContentHandlerFactory extends ApplicationListenerBase{

	private static List<ValueTextVo> notifyContentHandlerList = new ArrayList<>();
	
	private static Map<String, INotifyContentHandler> notifyContentHandlerMap = new HashMap<>();

	public static INotifyContentHandler getHandler(String handler) {
		return notifyContentHandlerMap.get(handler);
	}

	public static List<ValueTextVo> getNotifyContentHandlerList(){
		return notifyContentHandlerList;
	}
	
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		ApplicationContext context = event.getApplicationContext();
		Map<String, INotifyContentHandler> map = context.getBeansOfType(INotifyContentHandler.class);
		for(Entry<String, INotifyContentHandler> entry : map.entrySet()) {
			notifyContentHandlerMap.put(entry.getValue().getClassName(), entry.getValue());
			notifyContentHandlerList.add(new ValueTextVo(entry.getValue().getClassName(), entry.getValue().getName()));
		}
	}

	@Override
	protected void myInit() {
		
	}

}
