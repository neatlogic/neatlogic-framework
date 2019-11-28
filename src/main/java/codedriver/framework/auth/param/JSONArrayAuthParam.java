package codedriver.framework.auth.param;

import com.alibaba.fastjson.JSONArray;

public class JSONArrayAuthParam extends AuthParamBase {

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
	public AuthParamType getAuthType() {
		return AuthParamType.JSONARRAY;
	}

}
