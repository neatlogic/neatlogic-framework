package codedriver.framework.apiparam.validator;

import codedriver.framework.apiparam.core.ApiParamBase;
import codedriver.framework.apiparam.core.ApiParamType;

public class StringApiParam extends ApiParamBase {

	@Override
	public String getName() {

		return "字符串";
	}

	@Override
	public ApiParamType getType() {
		return ApiParamType.STRING;
	}

	@Override
	public boolean validate(Object param, String rule) {
		return true;
	}

}
