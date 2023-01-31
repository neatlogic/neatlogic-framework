package neatlogic.framework.exception.type;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class ApiRepeatException extends ApiRuntimeException {

	private static final long serialVersionUID = -8529977350164125804L;

	public ApiRepeatException(String msg) {
		super(msg);
	}

}
