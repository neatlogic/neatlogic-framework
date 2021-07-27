package codedriver.framework.matrix.exception;

import codedriver.framework.exception.core.ApiRuntimeException;

/**
 * @program: codedriver
 * @description:
 * @create: 2020-04-03 11:50
 **/
public class MatrixViewEditRowDataException extends ApiRuntimeException {
    private static final long serialVersionUID = -4508276752259783532L;

    public MatrixViewEditRowDataException() {
        super("视图矩阵不能编辑一行数据");
    }
}
