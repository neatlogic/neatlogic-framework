package codedriver.framework.exception.type;

import codedriver.framework.exception.core.ApiRuntimeException;

public class AnonymousExceptionMessage extends ApiRuntimeException {

	/** 
	* @Fields serialVersionUID : TODO 
	*/
	private static final long serialVersionUID = -6573198327767712927L;

	public AnonymousExceptionMessage() {
		super("不允许匿名访问");
	}

}
