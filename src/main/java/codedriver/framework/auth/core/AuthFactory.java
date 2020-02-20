package codedriver.framework.auth.core;

import java.util.*;

import org.reflections.Reflections;

public class AuthFactory {
	private static Map<String, AuthBase> authMap = new HashMap<String, AuthBase>();
	private static Map<String, List<AuthBase>> authGroupMap = new HashMap<>();
	static {
		Reflections reflections = new Reflections("codedriver");
		Set<Class<? extends AuthBase>> authClass = reflections.getSubTypesOf(AuthBase.class);

		for (Class<? extends AuthBase> c : authClass) {
			try {
				AuthBase authIns = c.newInstance();
				authMap.put(authIns.getAuthName(), authIns);
				if (authGroupMap.containsKey(authIns.getAuthGroup())){
					authGroupMap.get(authIns.getAuthGroup()).add(authIns);
				}else {
					List<AuthBase> authList = new ArrayList<>();
					authList.add(authIns);
					authGroupMap.put(authIns.getAuthGroup(), authList);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static AuthBase getAuthInstance(String authName) {
		return authMap.get(authName);
	}

	public static Map getAuthGroupMap(){
		return authGroupMap;
	}
}
