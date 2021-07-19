package codedriver.framework.matrix.exception;

import codedriver.framework.exception.core.ApiRuntimeException;

/**
 * @program: codedriver
 * @description:
 * @create: 2020-04-03 11:50
 **/
public class MatrixExternalSaveAttributeException extends ApiRuntimeException {
    private static final long serialVersionUID = -4508274152299783032L;

    public MatrixExternalSaveAttributeException() {
        super("外部数据源矩阵不能保存属性");
    }
}
