package codedriver.framework.matrix.exception;

import codedriver.framework.exception.core.ApiRuntimeException;

/**
 * @program: codedriver
 * @description:
 * @create: 2020-04-01 17:38
 **/
public class MatrixImportException extends ApiRuntimeException {

    private static final long serialVersionUID = -4508274752209783532L;

    public MatrixImportException(String msg) {
        super(msg);
    }
}
