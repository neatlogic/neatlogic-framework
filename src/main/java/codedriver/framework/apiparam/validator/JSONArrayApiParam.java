package codedriver.framework.apiparam.validator;

import com.alibaba.fastjson.JSONArray;

import codedriver.framework.common.constvalue.ApiParamType;
import codedriver.framework.param.validate.core.ApiParamValidatorBase;
import com.alibaba.fastjson.JSONObject;

public class JSONArrayApiParam extends ApiParamValidatorBase {

	@Override
	public String getName() {

		return "json数组";
	}

	@Override
	public boolean validate(Object param, String rule) {
		try {
			JSONArray.parseArray(JSONObject.toJSONString(param));
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
