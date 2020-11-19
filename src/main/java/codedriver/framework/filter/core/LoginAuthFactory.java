package codedriver.framework.filter.core;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;

import codedriver.framework.applicationlistener.core.ApplicationListenerBase;
import codedriver.framework.common.RootComponent;

@RootComponent
public class LoginAuthFactory extends ApplicationListenerBase{
	private static final Map<String,ILoginAuth> loginAuthMap = new HashMap<>();
	
	public static Map<String, ILoginAuth> getLoginAuthMap() {
		return loginAuthMap;
	}
	
	public static final ILoginAuth getLoginAuth(String type) {
		return loginAuthMap.get(type.toUpperCase());
	}

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		ApplicationContext context = event.getApplicationContext();
		Map<String, ILoginAuth> myMap = context.getBeansOfType(ILoginAuth.class);
		for (Map.Entry<String, ILoginAuth> entry : myMap.entrySet()) {
		    ILoginAuth authAuth = entry.getValue();
			loginAuthMap.put(authAuth.getType().toUpperCase(), authAuth);
		}
		
	}

	@Override
	protected void myInit() {
		// TODO Auto-generated method stub
		
	}

}
