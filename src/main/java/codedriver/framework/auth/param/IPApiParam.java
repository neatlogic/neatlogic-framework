package codedriver.framework.auth.param;

import java.util.regex.Pattern;

public class IPApiParam extends ApiParamBase {

	@Override
	public String getAuthName() {

		return "IP参数认证";
	}

	@Override
	public boolean doAuth(String param) {
		Pattern pattern = Pattern.compile("^((\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5]" + "|[*])\\.){3}(\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5]|[*])$");
		return pattern.matcher(param).matches();
	}

	@Override
	public ApiParamType getAuthType() {
		return ApiParamType.IP;
	}

}
