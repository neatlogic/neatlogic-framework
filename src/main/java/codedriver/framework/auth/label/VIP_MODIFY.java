package codedriver.framework.auth.label;

import codedriver.framework.auth.core.AuthBase;

public class VIP_MODIFY extends AuthBase {

	@Override
	public String getAuthDisplayName() {
		return "VIP管理权限";
	}

	@Override
	public String getAuthIntroduction() {
		return "vip用户拥有更高的工单优先级和SLA响应权限";
	}

	@Override
	public String getAuthGroup() {
		return "framework";
	}
}
