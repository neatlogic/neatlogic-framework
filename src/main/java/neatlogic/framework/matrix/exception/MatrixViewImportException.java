package neatlogic.framework.matrix.exception;

import neatlogic.framework.exception.core.ApiRuntimeException;

/**
 * @program: neatlogic
 * @description:
 * @create: 2020-04-01 17:38
 **/
public class MatrixViewImportException extends ApiRuntimeException {

    private static final long serialVersionUID = -4508124752209784332L;

    public MatrixViewImportException() {
        super("视图矩阵不支持导入");
    }
}
