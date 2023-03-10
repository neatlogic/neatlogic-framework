package neatlogic.framework.matrix.exception;

import neatlogic.framework.exception.core.ApiRuntimeException;

/**
 * @program: neatlogic
 * @description:
 * @create: 2020-04-01 17:38
 **/
public class MatrixNameDifferentImportFileNameException extends ApiRuntimeException {

    private static final long serialVersionUID = -4508274752209783532L;

    public MatrixNameDifferentImportFileNameException() {
        super("矩阵名称与导入文件名不同，不能导入");
    }
}
