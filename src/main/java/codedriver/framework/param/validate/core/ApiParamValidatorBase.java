package codedriver.framework.param.validate.core;

import codedriver.framework.common.constvalue.ApiParamType;

public abstract class ApiParamValidatorBase {
	public abstract String getName();

	public abstract boolean validate(Object param, String rule);

	public abstract ApiParamType getType();
}
