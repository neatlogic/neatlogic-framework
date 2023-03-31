package neatlogic.framework.matrix.exception;

import neatlogic.framework.exception.core.ApiRuntimeException;

/**
 * @program: neatlogic
 * @description:
 * @create: 2020-04-09 11:03
 **/
public class MatrixNotFoundException extends ApiRuntimeException {
    private static final long serialVersionUID = -4508274752209783532L;

    public MatrixNotFoundException(String matrixName) {
        super("exception.framework.matrixnotfoundexception", matrixName);
    }
}
