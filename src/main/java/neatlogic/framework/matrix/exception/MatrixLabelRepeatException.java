package neatlogic.framework.matrix.exception;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class MatrixLabelRepeatException extends ApiRuntimeException {

    private static final long serialVersionUID = 1460070825625680323L;

    public MatrixLabelRepeatException(String label) {
        super("矩阵:“{0}”已存在", label);
    }
}
