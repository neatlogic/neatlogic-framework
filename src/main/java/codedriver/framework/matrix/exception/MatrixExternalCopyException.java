package codedriver.framework.matrix.exception;

import codedriver.framework.exception.core.ApiRuntimeException;

/**
 * @program: codedriver
 * @description:
 * @create: 2020-04-03 11:50
 **/
public class MatrixExternalCopyException extends ApiRuntimeException {
    private static final long serialVersionUID = -4508234725299783532L;

    public MatrixExternalCopyException() {
        super("外部数据源矩阵不能复制");
    }
}
