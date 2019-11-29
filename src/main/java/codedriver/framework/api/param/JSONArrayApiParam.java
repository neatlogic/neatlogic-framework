package codedriver.framework.api.param;

import com.alibaba.fastjson.JSONArray;

import codedriver.framework.api.core.ApiParamBase;
import codedriver.framework.api.core.ApiParamType;

public class JSONArrayApiParam extends ApiParamBase {

	@Override
	public String getName() {

		return "json数组";
	}

	@Override
	public boolean validate(Object param) {
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
