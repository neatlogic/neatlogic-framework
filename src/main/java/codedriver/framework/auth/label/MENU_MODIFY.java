package codedriver.framework.auth.label;

import codedriver.framework.auth.core.AuthBase;
import codedriver.framework.auth.core.AuthGroupEnum;

public class MENU_MODIFY extends AuthBase {

	@Override
	public String getAuthDisplayName() {
		return "菜单管理权限";
	}

	@Override
	public String getAuthIntroduction() {
		return "对菜单进行添加、修改和删除";
	}

	@Override
	public String getAuthGroup() {
		return AuthGroupEnum.FRAMEWORK.getValue();
	}
}
