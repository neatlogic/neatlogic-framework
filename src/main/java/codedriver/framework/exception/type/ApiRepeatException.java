package codedriver.framework.exception.type;

import codedriver.framework.exception.core.ApiFieldValidRuntimeException;

public class ApiRepeatException extends ApiFieldValidRuntimeException {

	private static final long serialVersionUID = -8529977350164125804L;

	public ApiRepeatException(String msg) {
		super(msg);
	}

}
