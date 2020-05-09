package codedriver.framework.auth.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.reflections.Reflections;

import codedriver.framework.common.util.ModuleUtil;
import codedriver.framework.exception.auth.NoAuthGroupException;

public class AuthFactory {
	private static final Log logger = LogFactory.getLog(AuthFactory.class);
	private static Map<String, AuthBase> authMap = new HashMap<String, AuthBase>();
	private static Map<String, List<AuthBase>> authGroupMap = new HashMap<>();
	static {
		Reflections reflections = new Reflections("codedriver");
		Set<Class<? extends AuthBase>> authClass = reflections.getSubTypesOf(AuthBase.class);

		for (Class<? extends AuthBase> c : authClass) {
			try {
				AuthBase authIns = c.newInstance();
				authMap.put(authIns.getAuthName(), authIns);
				if(ModuleUtil.getModuleGroup(authIns.getAuthGroup()) == null) {
					throw new NoAuthGroupException(authIns.getAuthGroup());
				}
				if (authGroupMap.containsKey(authIns.getAuthGroup())){
					authGroupMap.get(authIns.getAuthGroup()).add(authIns);
				}else {
					List<AuthBase> authList = new ArrayList<>();
					authList.add(authIns);
					authGroupMap.put(authIns.getAuthGroup(), authList);
				}
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
				e.printStackTrace();
			}
		}
	}

	public static AuthBase getAuthInstance(String authName) {
		return authMap.get(authName);
	}

	public static Map<String, List<AuthBase>> getAuthGroupMap(){
		return authGroupMap;
	}
}
