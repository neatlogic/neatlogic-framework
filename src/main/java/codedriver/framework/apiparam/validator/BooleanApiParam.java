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
		if("true".equalsIgnoreCase(param.toString()) || "false".equalsIgnoreCase(param.toString())){
			return true;
		}else {
			return false;
		}
	}

	@Override
	public ApiParamType getType() {
		return ApiParamType.BOOLEAN;
	}

}
