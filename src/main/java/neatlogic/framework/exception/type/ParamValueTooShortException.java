package neatlogic.framework.exception.type;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class ParamValueTooShortException extends ApiRuntimeException {

    private static final long serialVersionUID = 327233755333422456L;

    public ParamValueTooShortException(String paramName, int valueLength, int maxLength) {
        super("exception.framework.paramvaluetooshortexception", paramName, maxLength);
    }
}
