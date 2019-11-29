package codedriver.framework.common.apiparam;

public class NoApiParam extends ApiParamBase {

	@Override
	public String getAuthName() {

		return "无需参数认证";
	}

	@Override
	public boolean doAuth(String param) {
		return true;
	}

	@Override
	public ApiParamType getAuthType() {
		return ApiParamType.NOAUTH;
	}

}
