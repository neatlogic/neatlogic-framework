package neatlogic.framework.exception.user;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class NoTenantException extends ApiRuntimeException {

	private static final long serialVersionUID = -3143105786239865612L;

	public NoTenantException() {
		super("没有找到任何租户信息");
	}
}
