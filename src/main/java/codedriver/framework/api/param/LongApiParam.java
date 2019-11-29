package codedriver.framework.api.param;

import codedriver.framework.api.core.ApiParamBase;
import codedriver.framework.api.core.ApiParamType;

public class LongApiParam extends ApiParamBase {

	@Override
	public String getAuthName() {

		return "整形参数认证";
	}

	@Override
	public boolean doAuth(String param) {
		try {
			Long.valueOf(param);
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

	@Override
	public ApiParamType getAuthType() {
		return ApiParamType.LONG;
	}

}
