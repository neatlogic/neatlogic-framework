package codedriver.framework.auth.label;

import codedriver.framework.auth.core.AuthBase;

public class ROLE_MODIFY extends AuthBase {

	@Override
	public String getAuthDisplayName() {
		return "角色管理权限";
	}

	@Override
	public String getAuthIntroduction() {
		return "对角色进行添加、修改和删除";
	}

}
