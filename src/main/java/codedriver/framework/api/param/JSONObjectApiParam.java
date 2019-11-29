package codedriver.framework.api.param;

import com.alibaba.fastjson.JSONObject;

import codedriver.framework.api.core.ApiParamBase;
import codedriver.framework.api.core.ApiParamType;

public class JSONObjectApiParam extends ApiParamBase {

	@Override
	public String getName() {
		return "json对象";
	}

	@Override
	public boolean validate(Object param) {
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
