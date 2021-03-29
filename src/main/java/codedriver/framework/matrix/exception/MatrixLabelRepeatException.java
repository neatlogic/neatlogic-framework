package codedriver.framework.matrix.exception;

import codedriver.framework.exception.core.ApiRuntimeException;

public class MatrixLabelRepeatException extends ApiRuntimeException {

	private static final long serialVersionUID = 1460070825625680323L;

	public MatrixLabelRepeatException(String label) {
		super("矩阵:'" + label + "'已存在");
	}
}
