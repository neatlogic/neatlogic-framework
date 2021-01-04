package codedriver.framework.auth.label;

import codedriver.framework.auth.core.AuthBase;

public class NO_AUTH extends AuthBase {

	@Override
	public String getAuthDisplayName() {
		return "无需权限校验";
	}

	@Override
	public String getAuthIntroduction() {
		return "无需校验权限";
	}

	@Override
	public String getAuthGroup() {
		return "framework";
	}
}
