package neatlogic.framework.exception.auth;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class LicenseAuthNotFoundException extends ApiRuntimeException {

	private static final long serialVersionUID = 2541167837356991977L;

	public LicenseAuthNotFoundException(String authName) {
		super("license 权限：'" + authName + "'不存在");
	}
}
