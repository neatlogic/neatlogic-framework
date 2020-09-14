package codedriver.framework.matrix.exception;

import codedriver.framework.exception.core.ApiRuntimeException;

/**
 * @program: codedriver
 * @description:
 * @create: 2020-04-09 11:03
 **/
public class MatrixNotFoundException extends ApiRuntimeException {
    private static final long serialVersionUID = -4508274752209783532L;

    public MatrixNotFoundException(String matrixName) {
        super("矩阵" + matrixName + "不存在");
    }
}
