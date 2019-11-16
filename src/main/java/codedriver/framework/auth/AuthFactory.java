package codedriver.framework.auth;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;

import codedriver.framework.common.auth.AuthBase;

public class AuthFactory {
	private static Map<String, AuthBase> authMap = new HashMap<String, AuthBase>();
	
	static {
		Reflections reflections = new Reflections("codedriver");
		Set<Class<? extends AuthBase>> authClass = reflections.getSubTypesOf(AuthBase.class);
		
		for (Class<? extends AuthBase> c: authClass) {
			try {
				AuthBase authIns = c.newInstance();
				authMap.put(authIns.getAuthName(), authIns);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static AuthBase getAuthInstance(String authName) {
		return authMap.get(authName);
	}
}
