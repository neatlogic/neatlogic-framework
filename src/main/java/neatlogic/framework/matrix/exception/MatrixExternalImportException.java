package neatlogic.framework.matrix.exception;

import neatlogic.framework.exception.core.ApiRuntimeException;

/**
 * @program: neatlogic
 * @description:
 * @create: 2020-04-01 17:38
 **/
public class MatrixExternalImportException extends ApiRuntimeException {

    private static final long serialVersionUID = -4508124752209784332L;

    public MatrixExternalImportException() {
        super("外部数据源矩阵不支持导入");
    }
}
