package codedriver.framework.auth.param;

public class NoAuthParam extends AuthParamBase {

	@Override
	public String getAuthName() {

		return "无需参数认证";
	}

	@Override
	public boolean doAuth(String param) {
		return true;
	}

	@Override
	public AuthParamType getAuthType() {
		return AuthParamType.NOAUTH;
	}

}
