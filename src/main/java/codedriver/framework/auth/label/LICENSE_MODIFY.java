/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.auth.label;

import codedriver.framework.auth.core.AuthBase;

public class LICENSE_MODIFY extends AuthBase {

	@Override
	public String getAuthDisplayName() {
		return "许可管理权限";
	}

	@Override
	public String getAuthIntroduction() {
		return "查看和更新许可";
	}

	@Override
	public String getAuthGroup() {
		return "framework";
	}

	@Override
	public Integer getSort() {
		return 25;
	}
}
