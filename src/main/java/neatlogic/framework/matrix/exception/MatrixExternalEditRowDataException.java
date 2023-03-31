package neatlogic.framework.matrix.exception;

import neatlogic.framework.exception.core.ApiRuntimeException;

/**
 * @program: neatlogic
 * @description:
 * @create: 2020-04-03 11:50
 **/
public class MatrixExternalEditRowDataException extends ApiRuntimeException {
    private static final long serialVersionUID = -4508276752259783532L;

    public MatrixExternalEditRowDataException() {
        super("exception.framework.matrixexternaleditrowdataexception");
    }
}
