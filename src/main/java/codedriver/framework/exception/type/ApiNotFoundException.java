package codedriver.framework.exception.type;

import codedriver.framework.exception.core.ApiRuntimeException;

public class ApiNotFoundException extends ApiRuntimeException {

	public ApiNotFoundException(String msg) {
		super(msg);
	}

}
