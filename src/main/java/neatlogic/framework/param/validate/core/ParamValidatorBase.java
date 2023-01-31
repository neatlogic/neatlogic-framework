package neatlogic.framework.param.validate.core;

import neatlogic.framework.common.constvalue.ParamType;

public abstract class ParamValidatorBase {
	public abstract String getName();

	public abstract boolean validate(Object param, String rule);

	public abstract ParamType getType();
}
