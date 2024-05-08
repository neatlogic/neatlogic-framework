package neatlogic.framework.exception.type;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class ParamSizeTooLargeException extends ApiRuntimeException {

    private static final long serialVersionUID = 327233755333422456L;

    public ParamSizeTooLargeException(String paramName, int valueLength, int maxSize) {
        super("nfet.paramsizetoolargeexception.paramsizetoolargeexception",  paramName, maxSize, valueLength);
    }
}
