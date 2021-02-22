package codedriver.framework.matrix.exception;

import codedriver.framework.exception.core.ApiFieldValidRuntimeException;

public class MatrixNameRepeatException extends ApiFieldValidRuntimeException {

	private static final long serialVersionUID = -9118655742835275741L;

	public MatrixNameRepeatException(String name) {
		super("矩阵:'" + name + "'已存在");
	}
}
