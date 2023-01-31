package neatlogic.framework.exception.auth;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class AuthNotFoundException extends ApiRuntimeException {

	private static final long serialVersionUID = 2541167837356991977L;

	public AuthNotFoundException(String authName) {
		super("权限：'" + authName + "'不存在");
	}
}
