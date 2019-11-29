package codedriver.framework.api.core;

public abstract class ApiParamBase {
	public abstract String getName();

	public abstract boolean validate(Object param);

	public abstract ApiParamType getType();
}
