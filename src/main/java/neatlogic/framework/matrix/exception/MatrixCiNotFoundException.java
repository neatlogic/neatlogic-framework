package neatlogic.framework.matrix.exception;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class MatrixCiNotFoundException extends ApiRuntimeException {

    private static final long serialVersionUID = 1808428617492097984L;

    public MatrixCiNotFoundException(String name) {
        super("exception.framework.matrixcinotfoundexception", name);
    }
}
