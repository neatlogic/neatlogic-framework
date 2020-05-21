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
			List<String> roleUuidList = authGroupMapper.getRoleUuidListByAuthGroupName(action);
			if (roleUuidList != null && roleUuidList.size() > 0) {
				for (String roleUuid : roleUuidList) {
					if (null != userContext.getRoleUuidList() && userContext.getRoleUuidList().contains(roleUuid)) {
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
