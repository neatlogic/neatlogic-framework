package codedriver.framework.auth.label;

import codedriver.framework.auth.core.AuthBase;

public class USER_MODIFY extends AuthBase {

	@Override
	public String getAuthDisplayName() {
		return "用户管理权限";
	}

	@Override
	public String getAuthIntroduction() {
		return "对用户进行添加、修改和删除";
	}

}
