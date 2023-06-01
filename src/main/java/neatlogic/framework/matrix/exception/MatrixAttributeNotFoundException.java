package neatlogic.framework.matrix.exception;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class MatrixAttributeNotFoundException extends ApiRuntimeException {

    private static final long serialVersionUID = -3699134556854285155L;

    public MatrixAttributeNotFoundException(String matrixUuid, String attributeUuid) {
        super("在{0}矩阵中属性：“{1}”不存在", matrixUuid, attributeUuid);
    }
}
