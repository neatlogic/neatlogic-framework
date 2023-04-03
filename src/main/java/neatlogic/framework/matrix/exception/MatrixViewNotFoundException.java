package neatlogic.framework.matrix.exception;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class MatrixViewNotFoundException extends ApiRuntimeException {

    private static final long serialVersionUID = 1708428617192097984L;

    public MatrixViewNotFoundException(String name) {
        super("exception.framework.matrixviewnotfoundexception", name);
    }
}
