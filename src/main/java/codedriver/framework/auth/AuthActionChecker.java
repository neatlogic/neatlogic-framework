package codedriver.framework.auth;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import codedriver.framework.common.RootComponent;
import codedriver.framework.dao.mapper.LabelMapper;
import codedriver.framework.threadlocal.UserContext;


@RootComponent
public class AuthActionChecker {
	private static LabelMapper labelMapper;

	@Autowired
	public void setLabelMapper(LabelMapper _labelMapper) {
		labelMapper = _labelMapper;
	}

	public static Boolean check(String action) {
		UserContext context = UserContext.get();
		if (context != null) {
			List<String> roleNameList = labelMapper.getRoleNameListByLabel(action);
			if (roleNameList != null && roleNameList.size() > 0) {
				for (String roleName : roleNameList) {
					if (context.getRoleNameList().contains(roleName)) {
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
