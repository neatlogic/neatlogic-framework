package codedriver.framework.auth.param;


public abstract class ApiParamBase {
	public abstract String getAuthName();
	public abstract boolean doAuth(String param);
	public abstract ApiParamType getAuthType();
}
