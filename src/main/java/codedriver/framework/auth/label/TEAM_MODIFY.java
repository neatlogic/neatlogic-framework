package codedriver.framework.auth.label;

import codedriver.framework.auth.core.AuthBase;

public class TEAM_MODIFY extends AuthBase {

	@Override
	public String getAuthDisplayName() {
		return "分组管理权限";
	}

	@Override
	public String getAuthIntroduction() {
		return "对分组进行添加、修改和删除";
	}

	@Override
	public String getAuthGroup() {
		return "framework";
	}
}
