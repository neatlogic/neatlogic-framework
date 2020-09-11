package codedriver.framework.matrix.exception;

import codedriver.framework.exception.core.ApiRuntimeException;

/**
 * @program: codedriver
 * @description:
 * @create: 2020-04-09 11:17
 **/
public class MatrixFileNotFoundException extends ApiRuntimeException {
    private static final long serialVersionUID = -4508274752209783532L;

    public MatrixFileNotFoundException() {
        super("矩阵导入文件不存在");
    }
}
