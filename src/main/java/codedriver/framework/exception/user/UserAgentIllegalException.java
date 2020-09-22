package codedriver.framework.exception.user;

import codedriver.framework.exception.core.ApiRuntimeException;

public class UserAgentIllegalException extends ApiRuntimeException {

	private static final long serialVersionUID = 8784255855238341892L;

	public UserAgentIllegalException(String msg) {
		super(msg);
	}
}
