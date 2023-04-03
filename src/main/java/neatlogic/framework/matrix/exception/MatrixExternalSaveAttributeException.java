package neatlogic.framework.matrix.exception;

import neatlogic.framework.exception.core.ApiRuntimeException;

/**
 * @program: neatlogic
 * @description:
 * @create: 2020-04-03 11:50
 **/
public class MatrixExternalSaveAttributeException extends ApiRuntimeException {
    private static final long serialVersionUID = -4508274152299783032L;

    public MatrixExternalSaveAttributeException() {
        super("exception.framework.matrixexternalsaveattributeexception");
    }
}
