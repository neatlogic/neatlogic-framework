package codedriver.framework.param.validate.core;

import codedriver.framework.common.constvalue.ParamType;

public abstract class ParamValidatorBase {
	public abstract String getName();

	public abstract boolean validate(Object param, String rule);

	public abstract ParamType getType();
}
