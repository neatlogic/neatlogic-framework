package codedriver.framework.auth.param;

public class LongAuthParam extends AuthParamBase {

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
	public AuthParamType getAuthType() {
		return AuthParamType.LONG;
	}

}
