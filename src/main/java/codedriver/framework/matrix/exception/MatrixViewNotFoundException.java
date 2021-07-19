package codedriver.framework.matrix.exception;

import codedriver.framework.exception.core.ApiRuntimeException;

public class MatrixViewNotFoundException extends ApiRuntimeException {

	private static final long serialVersionUID = 1708428617192097984L;

	public MatrixViewNotFoundException(String name) {
		super("矩阵：'" + name + "的视图配置信息不存在");
	}
}
