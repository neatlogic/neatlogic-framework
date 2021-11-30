package codedriver.framework.exception.type;

import codedriver.framework.exception.core.ApiRuntimeException;

public class ApiNotFoundException extends ApiRuntimeException {

	/** 
	* @Fields serialVersionUID : TODO 
	*/
	private static final long serialVersionUID = -8529977350164125804L;

	public ApiNotFoundException(String token) {
		super("token为“" + token + "”的接口不存在或已被禁用");
	}
}
