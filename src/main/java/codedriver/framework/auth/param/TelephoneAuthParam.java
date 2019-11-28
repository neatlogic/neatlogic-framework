package codedriver.framework.auth.param;

import java.util.regex.Pattern;

public class TelephoneAuthParam extends AuthParamBase {

	@Override
	public String getAuthName() {

		return "手机号码参数认证";
	}

	@Override
	public boolean doAuth(String param) {
		Pattern pattern = Pattern.compile("^1(3|4|5|7|8)\\d{9}$");
		return pattern.matcher(param).matches();
	}

	@Override
	public AuthParamType getAuthType() {
		return AuthParamType.TELEPHONE;
	}

}
