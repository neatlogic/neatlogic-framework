package neatlogic.framework.exception.user;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class NoTenantException extends ApiRuntimeException {

	private static final long serialVersionUID = -3143105786239865612L;

	public NoTenantException() {
		super("exception.framework.notenantexception");
	}
}
