package codedriver.framework.apiparam.validator;

import codedriver.framework.common.constvalue.ApiParamType;
import codedriver.framework.param.validate.core.ApiParamValidatorBase;

public class BooleanApiParam extends ApiParamValidatorBase {

	@Override
	public String getName() {
		return "布尔型";
	}

	@Override
	public boolean validate(Object param, String rule) {
		if(Boolean.TRUE.toString().equalsIgnoreCase(param.toString()) || Boolean.FALSE.toString().equalsIgnoreCase(param.toString())){
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
