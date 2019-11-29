package codedriver.framework.auth.label;

import codedriver.framework.auth.core.AuthBase;

public class SYSTEM_TEAM_EDIT extends AuthBase{

	@Override
	public String getAuthDisplayName() {
		return "组织架构管理权限";
	}

	@Override
	public String getAuthIntroduction() {
		return "拥有此权限才能查看，添加，修改和删除组织架构管理的内容。";
	}

	@Override
	public String getAuthName() {
		return "SYSTEM_TEAM_EDIT";
	}

}
