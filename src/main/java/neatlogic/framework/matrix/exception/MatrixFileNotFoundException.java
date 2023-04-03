package neatlogic.framework.matrix.exception;

import neatlogic.framework.exception.core.ApiRuntimeException;

/**
 * @program: neatlogic
 * @description:
 * @create: 2020-04-09 11:17
 **/
public class MatrixFileNotFoundException extends ApiRuntimeException {
    private static final long serialVersionUID = -4508274752209783532L;

    public MatrixFileNotFoundException() {
        super("exception.framework.matrixfilenotfoundexception");
    }
}
