package neatlogic.framework.matrix.exception;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class MatrixAttributeNotFoundException extends ApiRuntimeException {

    private static final long serialVersionUID = -3699134556854285155L;

    public MatrixAttributeNotFoundException(String matrixUuid, String attributeUuid) {
        super("exception.framework.matrixattributenotfoundexception", matrixUuid, attributeUuid);
    }
}
