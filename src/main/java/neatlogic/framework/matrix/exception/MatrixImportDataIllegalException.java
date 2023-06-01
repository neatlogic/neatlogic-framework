package neatlogic.framework.matrix.exception;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class MatrixImportDataIllegalException extends ApiRuntimeException {

    private static final long serialVersionUID = -1953517006925178125L;

    public MatrixImportDataIllegalException(int row, int col, String value) {
        super("第{0}行第{1}列数据“{2}”不合法", row, col, value);
    }
}
