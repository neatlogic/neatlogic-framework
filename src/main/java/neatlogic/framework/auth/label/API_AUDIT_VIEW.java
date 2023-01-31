/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.framework.auth.label;

import neatlogic.framework.auth.core.AuthBase;

public class API_AUDIT_VIEW extends AuthBase {

	@Override
	public String getAuthDisplayName() {
		return "查看操作审计权限";
	}

	@Override
	public String getAuthIntroduction() {
		return "查看操作审计权限";
	}

	@Override
	public String getAuthGroup() {
		return "framework";
	}

	@Override
	public Integer getSort() {
		return 8;
	}
}
