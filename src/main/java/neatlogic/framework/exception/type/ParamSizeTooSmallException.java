package neatlogic.framework.exception.type;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class ParamSizeTooSmallException extends ApiRuntimeException {

    private static final long serialVersionUID = 327233755333422456L;

    public ParamSizeTooSmallException(String paramName, int valueLength, int minSize) {
        super("nfet.paramsizetoosmallexception.paramsizetoosmallexception",  paramName, minSize, valueLength);
    }
}
