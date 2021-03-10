package codedriver.framework.matrix.exception;

import codedriver.framework.exception.core.ApiFieldValidRuntimeException;

public class MatrixLabelRepeatException extends ApiFieldValidRuntimeException {

	private static final long serialVersionUID = 1460070825625680323L;

	public MatrixLabelRepeatException(String label) {
		super("矩阵:'" + label + "'已存在");
	}
}
