package codedriver.framework.exception.type;

import codedriver.framework.exception.core.ApiRuntimeException;

public class ParamIrregularException extends ApiRuntimeException {
	public ParamIrregularException(String msg) {
		super(msg);
	}
}
