package neatlogic.framework.matrix.exception;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class MatrixNameRepeatException extends ApiRuntimeException {

    private static final long serialVersionUID = -9118655742835275741L;

    public MatrixNameRepeatException(String name) {
        super("矩阵:“{0}”已存在", name);
    }
}
