package codedriver.framework.auth.param;

public class StringAuthParam extends AuthParamBase {

	@Override
	public String getAuthName() {
		
		return "字符串参数认证";
	}



	@Override
	public AuthParamType getAuthType() {
		return AuthParamType.STRING;
	}



	@Override
	public boolean doAuth(String param) {
		// TODO Auto-generated method stub
		return true;
	}

}
