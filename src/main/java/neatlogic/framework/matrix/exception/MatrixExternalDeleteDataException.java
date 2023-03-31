package neatlogic.framework.matrix.exception;

import neatlogic.framework.exception.core.ApiRuntimeException;

/**
 * @program: neatlogic
 * @description:
 * @create: 2020-04-03 11:50
 **/
public class MatrixExternalDeleteDataException extends ApiRuntimeException {
    private static final long serialVersionUID = -4598274752809723572L;

    public MatrixExternalDeleteDataException() {
        super("exception.framework.matrixexternaldeletedataexception");
    }
}
