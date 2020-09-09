package codedriver.framework.matrix.exception;

import codedriver.framework.exception.core.ApiRuntimeException;

public class MatrixAttributeNotFoundException extends ApiRuntimeException {

	private static final long serialVersionUID = -3699134556854285155L;

	public MatrixAttributeNotFoundException(String matrixUuid, String attributeUuid) {
		super("在" + matrixUuid + "矩阵中属性：'" + attributeUuid + "'不存在");
	}
}
