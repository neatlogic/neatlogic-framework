package neatlogic.framework.param.validate.core;

import neatlogic.framework.common.constvalue.ApiParamType;

public abstract class ApiParamValidatorBase {
    public abstract String getName();

    public abstract boolean validate(Object param, String rule);

    public abstract ApiParamType getType();

}
