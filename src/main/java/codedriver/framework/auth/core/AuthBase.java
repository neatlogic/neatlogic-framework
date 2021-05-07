/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.auth.core;

public abstract class AuthBase {
	public final String getAuthName() {
		return this.getClass().getSimpleName();
	}

	public abstract String getAuthDisplayName();

	public abstract String getAuthIntroduction();

	public abstract String getAuthGroup();

	public abstract Integer getSort();


}
