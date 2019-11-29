package codedriver.framework.api.param;

import codedriver.framework.api.core.ApiParamBase;
import codedriver.framework.api.core.ApiParamType;

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
	public boolean validate(Object param) {
		return true;
	}

}
