package codedriver.framework.exception.type;

import codedriver.framework.apiparam.core.ApiParamType;
import codedriver.framework.exception.core.IApiExceptionMessage;

public class ParamIrregularExceptionMessage implements IApiExceptionMessage {
	private String paramName = "";
	private ApiParamType paramType = null;

	public ParamIrregularExceptionMessage(String _paramName) {
		this.paramName = _paramName;
	}

	public ParamIrregularExceptionMessage(String _paramName, ApiParamType _paramType) {
		this.paramName = _paramName;
		this.paramType = _paramType;
	}

	@Override
	public String getErrorCode() {
		return "05";
	}

	@Override
	public String getError() {
		return "参数“" + paramName + "”不符合格式要求";
	}

}
