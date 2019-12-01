package codedriver.framework.apiparam.core;

public abstract class ApiParamBase {
	public abstract String getName();

	public abstract boolean validate(Object param, String rule);

	public abstract ApiParamType getType();
}
