package codedriver.framework.matrix.exception;

import codedriver.framework.exception.core.ApiRuntimeException;

/**
 * @program: codedriver
 * @description:
 * @create: 2020-04-03 11:50
 **/
public class MatrixViewCopyException extends ApiRuntimeException {
    private static final long serialVersionUID = -4508234325299797532L;

    public MatrixViewCopyException() {
        super("视图矩阵不能复制");
    }
}
