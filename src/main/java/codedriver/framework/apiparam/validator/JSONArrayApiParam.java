package codedriver.framework.apiparam.validator;

import com.alibaba.fastjson.JSONArray;

import codedriver.framework.common.constvalue.ApiParamType;
import codedriver.framework.param.validate.core.ApiParamValidatorBase;

public class JSONArrayApiParam extends ApiParamValidatorBase {

	@Override
	public String getName() {

		return "json数组";
	}

	@Override
	public boolean validate(Object param, String rule) {
		try {
			JSONArray.parseArray(param.toString());
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

	@Override
	public ApiParamType getType() {
		return ApiParamType.JSONARRAY;
	}

}
