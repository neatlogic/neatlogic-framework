package neatlogic.framework.matrix.exception;

import neatlogic.framework.exception.core.ApiRuntimeException;

/**
 * @program: neatlogic
 * @description:
 * @create: 2020-04-03 11:50
 **/
public class MatrixExternalCopyException extends ApiRuntimeException {
    private static final long serialVersionUID = -4508234725299783532L;

    public MatrixExternalCopyException() {
        super("exception.framework.matrixexternalcopyexception");
    }
}
