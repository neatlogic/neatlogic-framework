package codedriver.framework.auth.param;

import com.alibaba.fastjson.JSONObject;

public class JSONObjectAuthParam extends AuthParamBase {

	@Override
	public String getAuthName() {

		return "json参数认证";
	}

	@Override
	public boolean doAuth(String param) {
		try {
			JSONObject.parseObject(param);
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

	@Override
	public AuthParamType getAuthType() {
		return AuthParamType.JSONOBJECT;
	}

}
