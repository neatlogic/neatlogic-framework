package codedriver.framework.exception.user;

import codedriver.framework.exception.core.ApiRuntimeException;

public class NoUserException extends ApiRuntimeException {
	private static final long serialVersionUID = 4864658649046286690L;

	public NoUserException() {
		super("没有找到任何用户信息");
	}
}
