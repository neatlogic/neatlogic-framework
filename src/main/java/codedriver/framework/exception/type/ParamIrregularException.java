package codedriver.framework.exception.type;

import codedriver.framework.exception.core.ApiRuntimeException;

public class ParamIrregularException extends ApiRuntimeException {
	/** 
	* @Fields serialVersionUID : TODO 
	*/
	private static final long serialVersionUID = -6753541026185329206L;

	public ParamIrregularException(String paramName) {
		super("参数“" + paramName + "”不符合格式要求");
	}
	public ParamIrregularException(String paramName, String rule) {
		super("参数“" + paramName + "”不符合格式要求， 格式为：" + rule);
	}
}
