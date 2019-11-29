package codedriver.framework.api.param;

import com.alibaba.fastjson.JSONArray;

import codedriver.framework.api.core.ApiParamBase;
import codedriver.framework.api.core.ApiParamType;

public class JSONArrayApiParam extends ApiParamBase {

	@Override
	public String getAuthName() {

		return "json数组参数认证";
	}

	@Override
	public boolean doAuth(String param) {
		try {
			JSONArray.parseArray(param);
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

	@Override
	public ApiParamType getAuthType() {
		return ApiParamType.JSONARRAY;
	}

}
