package codedriver.framework.api.param;

import codedriver.framework.api.core.ApiParamBase;
import codedriver.framework.api.core.ApiParamType;

public class BooleanApiParam extends ApiParamBase {

	@Override
	public String getAuthName() {

		return "布尔参数认证";
	}

	@Override
	public boolean doAuth(String param) {
		try {
			Boolean.valueOf(param);
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

	@Override
	public ApiParamType getAuthType() {
		return ApiParamType.BOOLEAN;
	}

}
