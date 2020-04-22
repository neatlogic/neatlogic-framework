package codedriver.framework.auth.label;

import codedriver.framework.auth.core.AuthBase;
import codedriver.framework.common.constvalue.ModuleEnum;

public class INTEGRATION_MODIFY extends AuthBase {

	@Override
	public String getAuthDisplayName() {
		return "集成管理权限";
	}

	@Override
	public String getAuthIntroduction() {
		return "对集成进行添加、修改和删除";
	}

	@Override
	public String getAuthGroup() {
		return ModuleEnum.FRAMEWORK.getValue();
	}
}
