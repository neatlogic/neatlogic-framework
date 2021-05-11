package codedriver.framework.exception.type;

import codedriver.framework.exception.core.ApiRuntimeException;

public class ParamIrregularException extends ApiRuntimeException {
	/** 
	* @Fields serialVersionUID : TODO 
	*/
	private static final long serialVersionUID = -6753541026185329206L;

	public ParamIrregularException(String msg) {
		super(msg);
	}
	public ParamIrregularException(String paramName, boolean b) {
		super("参数“" + paramName + "”不符合格式要求");
	}
}
