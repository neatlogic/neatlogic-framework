package codedriver.framework.apiparam.validator;

import com.alibaba.fastjson.JSONObject;

import codedriver.framework.apiparam.core.ApiParamBase;
import codedriver.framework.apiparam.core.ApiParamType;

public class JSONObjectApiParam extends ApiParamBase {

	@Override
	public String getName() {
		return "json对象";
	}

	@Override
	public boolean validate(Object param, String rule) {
		try {
			JSONObject.parseObject(param.toString());
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
