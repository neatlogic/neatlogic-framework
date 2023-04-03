package neatlogic.framework.exception.type;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class ParamValueTooLongException extends ApiRuntimeException {
    /**
     * @Fields serialVersionUID : TODO
     */
    private static final long serialVersionUID = 5528197166107887380L;

    public ParamValueTooLongException(String paramName, int valueLength, int maxLength) {
        super("exception.framework.paramvaluetoolongexception", paramName, maxLength, valueLength);
    }
}
