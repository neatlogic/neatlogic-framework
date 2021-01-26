package codedriver.framework.auth.label;

import codedriver.framework.auth.core.AuthBase;

public class VIP_VIEW extends AuthBase {

	@Override
	public String getAuthDisplayName() {
		return "VIP查看权限";
	}

	@Override
	public String getAuthIntroduction() {
		return "vip用户拥有更高的工单优先级和SLA响应权限，并可看到用户头像的VIP标识";
	}

	@Override
	public String getAuthGroup() {
		return "framework";
	}
}
