package neatlogic.framework.exception.util;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class FreemarkerTransformException extends ApiRuntimeException {
    private static final long serialVersionUID = 3623906073721231804L;

    public FreemarkerTransformException(String message) {
        super("exception.framework.freemarkertransformexception", message);
    }
}
