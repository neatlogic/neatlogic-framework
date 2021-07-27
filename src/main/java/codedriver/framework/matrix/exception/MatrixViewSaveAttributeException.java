package codedriver.framework.matrix.exception;

import codedriver.framework.exception.core.ApiRuntimeException;

/**
 * @program: codedriver
 * @description:
 * @create: 2020-04-03 11:50
 **/
public class MatrixViewSaveAttributeException extends ApiRuntimeException {
    private static final long serialVersionUID = -4598274152233703032L;

    public MatrixViewSaveAttributeException() {
        super("视图矩阵不能保存属性");
    }
}
