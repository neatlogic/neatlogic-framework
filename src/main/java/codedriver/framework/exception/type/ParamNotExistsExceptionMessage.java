package codedriver.framework.exception.type;

import codedriver.framework.apiparam.core.ApiParamType;
import codedriver.framework.exception.core.IApiExceptionMessage;

public class ParamNotExistsExceptionMessage implements IApiExceptionMessage {
	private String paramName = "";

	public ParamNotExistsExceptionMessage(String _paramName) {
		this.paramName = _paramName;
	}

	public ParamNotExistsExceptionMessage(String _paramName, ApiParamType _paramType) {
		this.paramName = _paramName;
	}

	@Override
	public String getErrorCode() {
		return "06";
	}

	@Override
	public String getError() {
		return "参数：\"" + paramName + "\"不存在";
	}

}
