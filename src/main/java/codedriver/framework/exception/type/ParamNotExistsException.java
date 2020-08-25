package codedriver.framework.exception.type;

import codedriver.framework.exception.core.ApiRuntimeException;

public class ParamNotExistsException extends ApiRuntimeException {
	/** 
	* @Fields serialVersionUID : TODO 
	*/
	private static final long serialVersionUID = 9091220382590565470L;

	public ParamNotExistsException(String msg) {
		super(msg);
	}
}
