package codedriver.framework.common.apiparam;

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
