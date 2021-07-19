package codedriver.framework.matrix.exception;

import codedriver.framework.exception.core.ApiRuntimeException;

/**
 * @program: codedriver
 * @description:
 * @create: 2020-04-03 11:50
 **/
public class MatrixExternalEditRowDataException extends ApiRuntimeException {
    private static final long serialVersionUID = -4508276752259783532L;

    public MatrixExternalEditRowDataException() {
        super("外部数据源矩阵不能编辑一行数据");
    }
}
