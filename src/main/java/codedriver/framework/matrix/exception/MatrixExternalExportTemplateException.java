package codedriver.framework.matrix.exception;

import codedriver.framework.exception.core.ApiRuntimeException;

/**
 * @program: codedriver
 * @description:
 * @create: 2020-04-03 11:50
 **/
public class MatrixExternalExportTemplateException extends ApiRuntimeException {
    private static final long serialVersionUID = -4507385752209783532L;

    public MatrixExternalExportTemplateException() {
        super("外部数据源矩阵不能导出模板");
    }
}
