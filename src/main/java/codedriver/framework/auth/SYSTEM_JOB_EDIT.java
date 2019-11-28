package codedriver.framework.auth;

import codedriver.framework.common.auth.AuthBase;

public class SYSTEM_JOB_EDIT extends AuthBase{

	@Override
	public String getAuthDisplayName() {
		return "作业管理权限";
	}

	@Override
	public String getAuthIntroduction() {
		return "拥有此权限才能查看，添加，修改和删除作业管理的内容。";
	}

	@Override
	public String getAuthName() {
		return "SYSTEM_JOB_EDIT";
	}

}
