package neatlogic.framework.auth.label;

import neatlogic.framework.auth.core.AuthBase;

/** 权限名称与说明使用国际化键，权限标识及校验规则保持不变。 */
public class VIP_VIEW extends AuthBase {

	@Override
	public String getAuthDisplayName() {
		return "auth.vip_view.name";
	}

	@Override
	public String getAuthIntroduction() {
		return "auth.vip_view.description";
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
