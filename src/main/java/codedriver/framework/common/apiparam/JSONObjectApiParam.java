package codedriver.framework.common.apiparam;

import com.alibaba.fastjson.JSONObject;

public class JSONObjectApiParam extends ApiParamBase {

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
	public ApiParamType getAuthType() {
		return ApiParamType.JSONOBJECT;
	}

}
