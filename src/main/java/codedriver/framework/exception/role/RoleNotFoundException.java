package codedriver.framework.exception.role;

import codedriver.framework.exception.core.ApiRuntimeException;

public class RoleNotFoundException extends ApiRuntimeException {
	private static final long serialVersionUID = -337491984654499964L;

	public RoleNotFoundException(String role) {
		super("角色：" + role + "不存在");
	}
}
