package codedriver.framework.auth.param;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;

public class AuthParamFactory {
	private static Map<AuthParamType, AuthParamBase> authParamMap = new HashMap<AuthParamType, AuthParamBase>();
	
	static {
		Reflections reflections = new Reflections("codedriver");
		Set<Class<? extends AuthParamBase>> authClass = reflections.getSubTypesOf(AuthParamBase.class);
		for (Class<? extends AuthParamBase> c: authClass) {
			try {
				AuthParamBase authIns = c.newInstance();
				authParamMap.put(authIns.getAuthType(), authIns);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static AuthParamBase getAuthInstance(AuthParamType authParamType) {
		return authParamMap.get(authParamType);
	}
}
