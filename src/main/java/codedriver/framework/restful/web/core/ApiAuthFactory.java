package codedriver.framework.restful.web.core;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;

import codedriver.framework.applicationlistener.core.ApplicationListenerBase;
import codedriver.framework.common.RootComponent;

@RootComponent
public class ApiAuthFactory extends ApplicationListenerBase{
	private static final Map<String,IApiAuth> apiAuthMap = new HashMap<>();
	
	public static Map<String, IApiAuth> getApiAuthMap() {
		return apiAuthMap;
	}
	
	public static final IApiAuth getApiAuth(String type) {
		return apiAuthMap.get(type.toUpperCase());
	}

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		ApplicationContext context = event.getApplicationContext();
		Map<String, IApiAuth> myMap = context.getBeansOfType(IApiAuth.class);
		for (Map.Entry<String, IApiAuth> entry : myMap.entrySet()) {
		    IApiAuth apiAuth = entry.getValue();
			apiAuthMap.put(apiAuth.getType().toUpperCase(), apiAuth);
		}
		
	}

	@Override
	protected void myInit() {
		// TODO Auto-generated method stub
		
	}

}
