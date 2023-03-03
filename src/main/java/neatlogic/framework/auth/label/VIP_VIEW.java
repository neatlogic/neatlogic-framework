package neatlogic.framework.auth.label;

import neatlogic.framework.auth.core.AuthBase;

public class VIP_VIEW extends AuthBase {

	@Override
	public String getAuthDisplayName() {
		return "auth.framework.vipview.name";
	}

	@Override
	public String getAuthIntroduction() {
		return "auth.framework.vipview.introduction";
	}

	@Override
	public String getAuthGroup() {
		return "framework";
	}

	@Override
	public Integer getSort() {
		return 2;
	}
}
