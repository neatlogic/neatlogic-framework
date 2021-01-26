package codedriver.framework.auth.label;

import codedriver.framework.auth.core.AuthBase;

public class VIP_VIEW extends AuthBase {

	@Override
	public String getAuthDisplayName() {
		return "VIP查看权限";
	}

	@Override
	public String getAuthIntroduction() {
		return "拥有此权限才能查看vip用户头像的VIP标识";
	}

	@Override
	public String getAuthGroup() {
		return "framework";
	}
}
