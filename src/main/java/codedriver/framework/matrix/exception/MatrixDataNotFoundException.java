package codedriver.framework.matrix.exception;

import codedriver.framework.exception.core.ApiRuntimeException;

/**
 * @program: codedriver
 * @description:
 * @create: 2020-04-09 11:21
 **/
public class MatrixDataNotFoundException extends ApiRuntimeException {
    private static final long serialVersionUID = -4508274752209783532L;

    public MatrixDataNotFoundException(String matrixName) {
        super("矩阵" + matrixName + "数据不存在");
    }
}
