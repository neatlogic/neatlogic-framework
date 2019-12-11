package codedriver.framework.exception.type;

import codedriver.framework.exception.core.ApiRuntimeException;

public class ComponentNotFoundException extends ApiRuntimeException {

	public ComponentNotFoundException(String msg) {
		super(msg);
	}

}
