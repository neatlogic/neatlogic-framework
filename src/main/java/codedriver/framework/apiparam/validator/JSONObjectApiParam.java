package codedriver.framework.apiparam.validator;

import com.alibaba.fastjson.JSONObject;

import codedriver.framework.common.constvalue.ApiParamType;
import codedriver.framework.param.validate.core.ApiParamValidatorBase;

public class JSONObjectApiParam extends ApiParamValidatorBase {

	@Override
	public String getName() {
		return "json对象";
	}

	@Override
	public boolean validate(Object param, String rule) {
		try {
			JSONObject.parseObject(JSONObject.toJSONString(param));
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

	@Override
	public ApiParamType getType() {
		return ApiParamType.JSONOBJECT;
	}

}
