package codedriver.framework.apiparam.validator;

import com.alibaba.fastjson.JSONArray;

import codedriver.framework.apiparam.core.ApiParamBase;
import codedriver.framework.apiparam.core.ApiParamType;

public class JSONArrayApiParam extends ApiParamBase {

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
