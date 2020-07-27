package codedriver.framework.exception.type;

import codedriver.framework.exception.core.ApiRuntimeException;

public class ApiRepeatException extends ApiRuntimeException {

	/**
	* @Fields serialVersionUID : TODO
	*/
	private static final long serialVersionUID = -8529977350164125804L;

	public ApiRepeatException(String msg) {
		super(msg);
	}

}
