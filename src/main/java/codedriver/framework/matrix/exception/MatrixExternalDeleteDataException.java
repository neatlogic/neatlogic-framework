package codedriver.framework.matrix.exception;

import codedriver.framework.exception.core.ApiRuntimeException;

/**
 * @program: codedriver
 * @description:
 * @create: 2020-04-03 11:50
 **/
public class MatrixExternalDeleteDataException extends ApiRuntimeException {
    private static final long serialVersionUID = -4598274752809723572L;

    public MatrixExternalDeleteDataException() {
        super("外部数据源矩阵不能删除数据");
    }
}
