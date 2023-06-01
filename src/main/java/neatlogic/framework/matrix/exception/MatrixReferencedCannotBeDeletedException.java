package neatlogic.framework.matrix.exception;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class MatrixReferencedCannotBeDeletedException extends ApiRuntimeException {

    private static final long serialVersionUID = 3459303366397256808L;

    public MatrixReferencedCannotBeDeletedException(String uuid) {
        super("矩阵：“{0}”有被引用，不能删除", uuid);
    }
}
