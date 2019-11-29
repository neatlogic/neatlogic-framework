package codedriver.framework.api.param;

import codedriver.framework.api.core.ApiParamBase;
import codedriver.framework.api.core.ApiParamType;

public class StringApiParam extends ApiParamBase {

	@Override
	public String getAuthName() {
		
		return "字符串参数认证";
	}



	@Override
	public ApiParamType getAuthType() {
		return ApiParamType.STRING;
	}



	@Override
	public boolean doAuth(String param) {
		// TODO Auto-generated method stub
		return true;
	}

}
