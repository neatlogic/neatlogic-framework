package codedriver.framework.auth.core;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import codedriver.framework.asynchronization.threadlocal.UserContext;
import codedriver.framework.common.RootComponent;
import codedriver.framework.dao.mapper.AuthGroupMapper;


@RootComponent
public class AuthActionChecker {
	private static AuthGroupMapper authGroupMapper;

	@Autowired
	public void setAuthGroupMapper(AuthGroupMapper _authGroupMapper) {
		authGroupMapper = _authGroupMapper;
	}

	public static Boolean check(String action) {
		UserContext userContext = UserContext.get();
		if (userContext != null) {
			List<String> roleNameList = authGroupMapper.getRoleNameListByAuthGroupName(action);
			if (roleNameList != null && roleNameList.size() > 0) {
				for (String roleName : roleNameList) {
					if (null != userContext.getRoleNameList()&&userContext.getRoleNameList().contains(roleName)) {
						return true;
					}
				}
				return false;
			} else {
				return true;
			}
		} else {
			return false;
		}
	}
}
