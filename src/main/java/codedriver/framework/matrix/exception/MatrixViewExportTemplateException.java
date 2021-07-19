package codedriver.framework.matrix.exception;

import codedriver.framework.exception.core.ApiRuntimeException;

/**
 * @program: codedriver
 * @description:
 * @create: 2020-04-03 11:50
 **/
public class MatrixViewExportTemplateException extends ApiRuntimeException {
    private static final long serialVersionUID = -4507385752209782623L;

    public MatrixViewExportTemplateException() {
        super("外部数据源矩阵不能导出模板");
    }
}
