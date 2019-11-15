package codedriver.framework.auth;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.asynchronization.threadlocal.UserContext;
import codedriver.framework.common.RootComponent;
import codedriver.framework.dao.mapper.LabelMapper;


@RootComponent
public class AuthActionChecker {
	private static LabelMapper labelMapper;

	@Autowired
	public void setLabelMapper(LabelMapper _labelMapper) {
		labelMapper = _labelMapper;
	}

	public static Boolean check(String action) {
		UserContext Ucontext = UserContext.get();
		if (Ucontext != null) {
			List<String> roleNameList = labelMapper.getRoleNameListByLabel(action);
			if (roleNameList != null && roleNameList.size() > 0) {
				for (String roleName : roleNameList) {
					if (Ucontext.getRoleNameList().contains(roleName)) {
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
