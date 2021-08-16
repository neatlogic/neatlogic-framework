/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.auth.core;

import codedriver.framework.common.util.ModuleUtil;
import codedriver.framework.exception.auth.NoAuthGroupException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.reflections.Reflections;

import java.util.*;

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
