package codedriver.framework.auth.param;


public abstract class AuthParamBase {
	public abstract String getAuthName();
	public abstract boolean doAuth(String param);
	public abstract AuthParamType getAuthType();
}
