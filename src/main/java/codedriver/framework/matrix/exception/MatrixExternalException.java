package codedriver.framework.matrix.exception;

import codedriver.framework.exception.core.ApiRuntimeException;

/**
 * @program: codedriver
 * @description:
 * @create: 2020-04-03 11:50
 **/
@Deprecated
public class MatrixExternalException extends ApiRuntimeException {
    private static final long serialVersionUID = -4508274752209783532L;

    public MatrixExternalException(String msg) {
        super(msg);
    }
}
