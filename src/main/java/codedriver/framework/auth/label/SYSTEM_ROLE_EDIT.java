package codedriver.framework.auth.label;

import codedriver.framework.auth.core.AuthBase;

public class SYSTEM_ROLE_EDIT extends AuthBase{

	@Override
	public String getAuthDisplayName() {
		return "角色管理权限";
	}

	@Override
	public String getAuthIntroduction() {
		return "拥有此权限才能查看，添加，修改和删除角色管理的内容。";
	}

	@Override
	public String getAuthName() {
		return "SYSTEM_ROLE_EDIT";
	}

}
