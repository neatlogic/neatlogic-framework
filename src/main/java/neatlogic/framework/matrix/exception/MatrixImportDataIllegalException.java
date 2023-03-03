package neatlogic.framework.matrix.exception;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class MatrixImportDataIllegalException extends ApiRuntimeException {

    private static final long serialVersionUID = -1953517006925178125L;

    public MatrixImportDataIllegalException(int row, int col, String value) {
        super("第" + row + "行第" + col + "列数据'" + value + "'不合法");
    }
}
