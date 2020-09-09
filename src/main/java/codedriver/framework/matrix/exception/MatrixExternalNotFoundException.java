package codedriver.framework.matrix.exception;

import codedriver.framework.exception.core.ApiRuntimeException;

public class MatrixExternalNotFoundException extends ApiRuntimeException {

	private static final long serialVersionUID = 1708428607162097984L;

	public MatrixExternalNotFoundException(String uuid) {
		super("矩阵外部数据源'" + uuid + "'信息不存在");
	}
}
