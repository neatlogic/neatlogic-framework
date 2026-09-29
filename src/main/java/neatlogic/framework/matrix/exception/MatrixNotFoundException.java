package neatlogic.framework.matrix.exception;

import neatlogic.framework.exception.core.ApiRuntimeException;

/**
 * @program: neatlogic
 * @description:
 * @create: 2020-04-09 11:03
 **/
public class MatrixNotFoundException extends ApiRuntimeException {
    private static final long serialVersionUID = -4508274752209783532L;

    /**
     * 报告指定矩阵不存在，矩阵名称或标识作为原始参数保留。
     *
     * @param matrixName 矩阵名称或标识
     */
    public MatrixNotFoundException(String matrixName) {
        super("nfme.matrixnotfoundexception.matrixnotfoundexception", matrixName);
    }
}
