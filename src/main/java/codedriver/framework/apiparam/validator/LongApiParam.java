package codedriver.framework.apiparam.validator;

import codedriver.framework.apiparam.core.ApiParamBase;
import codedriver.framework.apiparam.core.ApiParamType;

public class LongApiParam extends ApiParamBase {

	@Override
	public String getName() {
		return "长整数";
	}

	@Override
	public boolean validate(Object param, String rule) {
		try {
			Long.valueOf(param.toString());
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
