package codedriver.framework.matrix.exception;

import codedriver.framework.exception.core.ApiRuntimeException;

public class MatrixNameRepeatException extends ApiRuntimeException {

	private static final long serialVersionUID = -9118655742835275741L;

	public MatrixNameRepeatException(String name) {
		super("矩阵名 :'" + name + "'已存在");
	}
}
