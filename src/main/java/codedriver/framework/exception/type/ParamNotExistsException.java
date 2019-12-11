package codedriver.framework.exception.type;

import codedriver.framework.exception.core.ApiRuntimeException;

public class ParamNotExistsException extends ApiRuntimeException {
	public ParamNotExistsException(String msg) {
		super(msg);
	}
}
