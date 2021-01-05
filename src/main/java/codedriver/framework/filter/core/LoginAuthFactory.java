package codedriver.framework.filter.core;

import codedriver.framework.applicationlistener.core.ApplicationListenerBase;
import codedriver.framework.common.RootComponent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.HashMap;
import java.util.Map;

@RootComponent
public class LoginAuthFactory extends ApplicationListenerBase{
	private static final Map<String, ILoginAuthHandler> loginAuthMap = new HashMap<>();
	
	public static Map<String, ILoginAuthHandler> getLoginAuthMap() {
		return loginAuthMap;
	}
	
	public static final ILoginAuthHandler getLoginAuth(String type) {
		return loginAuthMap.get(type.toUpperCase());
	}

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		ApplicationContext context = event.getApplicationContext();
		Map<String, ILoginAuthHandler> myMap = context.getBeansOfType(ILoginAuthHandler.class);
		for (Map.Entry<String, ILoginAuthHandler> entry : myMap.entrySet()) {
		    ILoginAuthHandler authAuth = entry.getValue();
			loginAuthMap.put(authAuth.getType().toUpperCase(), authAuth);
		}
		
	}

	@Override
	protected void myInit() {
		// TODO Auto-generated method stub
		
	}

}
