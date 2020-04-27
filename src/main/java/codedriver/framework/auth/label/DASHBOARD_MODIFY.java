package codedriver.framework.auth.label;

import codedriver.framework.auth.core.AuthBase;
import codedriver.framework.common.constvalue.ModuleEnum;

public class DASHBOARD_MODIFY extends AuthBase {

	@Override
	public String getAuthDisplayName() {
		return "面板管理权限";
	}

	@Override
	public String getAuthIntroduction() {
		return "对系统类面板进行新增、编辑、授权和删除";
	}

	@Override
	public String getAuthGroup() {
		return ModuleEnum.FRAMEWORK.getValue();
	}
}
