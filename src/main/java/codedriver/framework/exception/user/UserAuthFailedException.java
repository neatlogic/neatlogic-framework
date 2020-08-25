package codedriver.framework.exception.user;

import codedriver.framework.exception.core.ApiRuntimeException;

public class UserAuthFailedException extends ApiRuntimeException {
	private static final long serialVersionUID = -1541151756865093959L;

	public UserAuthFailedException() {
		super("用户验证失败");
	}
}
