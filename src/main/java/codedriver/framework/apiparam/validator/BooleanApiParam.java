package codedriver.framework.apiparam.validator;

import codedriver.framework.apiparam.core.ApiParamBase;
import codedriver.framework.apiparam.core.ApiParamType;

public class BooleanApiParam extends ApiParamBase {

	@Override
	public String getName() {
		return "布尔型";
	}

	@Override
	public boolean validate(Object param, String rule) {
		try {
			Boolean.valueOf(param.toString());
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

	@Override
	public ApiParamType getType() {
		return ApiParamType.LONG;
	}

}
