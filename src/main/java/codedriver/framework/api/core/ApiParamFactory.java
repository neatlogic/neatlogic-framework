package codedriver.framework.api.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;

public class ApiParamFactory {
	private static Map<ApiParamType, ApiParamBase> authParamMap = new HashMap<ApiParamType, ApiParamBase>();
	
	static {
		Reflections reflections = new Reflections("codedriver");
		Set<Class<? extends ApiParamBase>> authClass = reflections.getSubTypesOf(ApiParamBase.class);
		for (Class<? extends ApiParamBase> c: authClass) {
			try {
				ApiParamBase authIns = c.newInstance();
				authParamMap.put(authIns.getType(), authIns);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static ApiParamBase getAuthInstance(ApiParamType authParamType) {
		return authParamMap.get(authParamType);
	}
}
