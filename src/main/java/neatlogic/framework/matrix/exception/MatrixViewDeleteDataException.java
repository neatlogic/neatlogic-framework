package neatlogic.framework.matrix.exception;

import neatlogic.framework.exception.core.ApiRuntimeException;

/**
 * @program: neatlogic
 * @description:
 * @create: 2020-04-03 11:50
 **/
public class MatrixViewDeleteDataException extends ApiRuntimeException {
    private static final long serialVersionUID = -4601274752209713532L;

    public MatrixViewDeleteDataException() {
        super("视图矩阵不能删除数据");
    }
}
