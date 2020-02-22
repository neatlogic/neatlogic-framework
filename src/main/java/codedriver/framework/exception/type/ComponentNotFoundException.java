package codedriver.framework.exception.type;

import codedriver.framework.exception.core.ApiRuntimeException;

public class ComponentNotFoundException extends ApiRuntimeException {

	/** 
	* @Fields serialVersionUID : TODO 
	*/
	private static final long serialVersionUID = -6165807991291970685L;

	public ComponentNotFoundException(String msg) {
		super(msg);
	}

}
