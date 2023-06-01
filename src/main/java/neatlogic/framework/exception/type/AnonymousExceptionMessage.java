package neatlogic.framework.exception.type;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class AnonymousExceptionMessage extends ApiRuntimeException {

	/** 
	* @Fields serialVersionUID : TODO 
	*/
	private static final long serialVersionUID = -6573198327767712927L;

	public AnonymousExceptionMessage() {
		super("不允许匿名访问");
	}

}
