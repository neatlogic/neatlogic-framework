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
	public ParamNotExistsException(String paramNames, boolean b) {
		super("参数：“" + paramNames + "”不能为空");
	}

	public ParamNotExistsException(String ... paramNames) {
		super("参数：“" + String.join("、", paramNames) + "”不能同时为空");
	}
}
