package codedriver.framework.api.core;


public abstract class ApiParamBase {
	public abstract String getAuthName();
	public abstract boolean doAuth(String param);
	public abstract ApiParamType getAuthType();
}
