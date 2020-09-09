package codedriver.framework.matrix.exception;

import codedriver.framework.exception.core.ApiRuntimeException;

/**
 * @program: codedriver
 * @description:
 * @create: 2020-04-09 11:06
 **/
public class MatrixHeaderMisMatchException extends ApiRuntimeException {
    private static final long serialVersionUID = -4508274752209783532L;

    public MatrixHeaderMisMatchException(String matrixName) {
        super("矩阵" + matrixName + "头信息不匹配");
    }
}
